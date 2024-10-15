package com.tfyre.netbeans.claude;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfyre.netbeans.claude.model.ClaudeRequest;
import com.tfyre.netbeans.claude.model.ClaudeResponse;
import com.tfyre.netbeans.claude.model.ImmutableMessage;
import com.tfyre.netbeans.claude.model.ImmutableClaudeRequest;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import javax.swing.SwingUtilities;
import org.netbeans.api.progress.ProgressHandle;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.RequestProcessor;

/**
 *
 * @author Francois Steyn - (fsteyn@tfyre.co.za)
 */
@ActionID(category = "Tools", id = "com.tfyre.netbeans.claudai.ClaudeAction")
@ActionRegistration(displayName = "#CTL_ClaudeAction")
@ActionReference(path = "Menu/Tools", position = 1300)
@Messages({
    "CTL_ClaudeAction=Ask Claude",
    "MSG_ApiKeyNotSet=Please set your Claude API Key in Tools > Options > Advanced > Claude",
    "MSG_EnterPrompt=Enter your question for Claude:",
    "TTL_AskClaude=Ask Claude",
    "MSG_QueryingClaude=Querying Claude",
    "ERR_ApiError=An error occurred while querying Claude",
    "# {0} - errorDetail",
    "ERR_ApiErrorDetails=Error details: {0}",
    "# {0} - errorDetail",
    "# {1} - source",
    "ERR_JsonParsingError=Error parsing JSON response: {0} - {1}"
})
public final class ClaudeAction implements ActionListener {

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    @Override
    public void actionPerformed(ActionEvent e) {
        final String apiKey = NbPreferences.forModule(ClaudeOptions.class).get("ClaudeApiKey", "");
        if (apiKey.isEmpty()) {
            showWarning(Bundle.MSG_ApiKeyNotSet());
            return;
        }

        final String userPrompt = getUserPrompt();
        if (userPrompt == null || userPrompt.trim().isEmpty()) {
            return;
        }

        queryClaudeAsync(userPrompt, apiKey);
    }

    private void showWarning(final String message) {
        DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                message,
                NotifyDescriptor.WARNING_MESSAGE
        ));
    }

    private String getUserPrompt() {
        final NotifyDescriptor.InputLine input = new NotifyDescriptor.InputLine(
                Bundle.MSG_EnterPrompt(),
                Bundle.TTL_AskClaude()
        );

        if (DialogDisplayer.getDefault().notify(input) == NotifyDescriptor.OK_OPTION) {
            return input.getInputText();
        }
        return null;
    }

    private void queryClaudeAsync(final String userPrompt, final String apiKey) {
        RequestProcessor.getDefault().post(() -> {
            try (final ProgressHandle handle = ProgressHandle.createHandle(Bundle.MSG_QueryingClaude())) {
                handle.start();
                final String response = queryClaude(userPrompt, apiKey);
                showInfo(response);
            } catch (Exception ex) {
                showError(ex);
            }
        });
    }

    private void showInfo(final String message) {
        SwingUtilities.invokeLater(() -> {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    message,
                    NotifyDescriptor.INFORMATION_MESSAGE
            ));
        });
    }

    private void showError(final Exception ex) {
        SwingUtilities.invokeLater(() -> {
            final String errorMessage = Bundle.ERR_ApiError() + "\n"
                    + Bundle.ERR_ApiErrorDetails(ex.getMessage());
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                    errorMessage,
                    NotifyDescriptor.ERROR_MESSAGE
            ));
        });
    }

    private String queryClaude(final String prompt, final String apiKey) throws Exception {
        final String modelApiValue = NbPreferences.forModule(ClaudeOptions.class).get("ClaudeModel", ClaudeModel.CLAUDE_3_OPUS_20240229.getApiValue());
        final ClaudeModel selectedModel = ClaudeModel.fromApiValue(modelApiValue);

        final ClaudeRequest request = ImmutableClaudeRequest.builder()
                .model(selectedModel.getApiValue())
                .maxTokens(1000)
                .messages(List.of(ImmutableMessage.builder()
                        .role("user")
                        .content(prompt)
                        .build()))
                .build();

        final String jsonInputString = OBJECT_MAPPER.writeValueAsString(request);

        final HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(CLAUDE_API_URL))
                .header("Content-Type", "application/json")
                .header("anthropic-version", "2023-06-01")
                .header("x-api-key", apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(jsonInputString))
                .build();

        final HttpResponse<String> httpResponse = HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (httpResponse.statusCode() != 200) {
            throw new IOException("HTTP error code: %d - %s".formatted(httpResponse.statusCode(), httpResponse.body()));
        }

        final ClaudeResponse response;
        try {
            response = OBJECT_MAPPER.readValue(httpResponse.body(), ClaudeResponse.class);
        } catch (JsonProcessingException e) {
            throw new IOException(Bundle.ERR_JsonParsingError(e.getMessage(), httpResponse.body()), e);
        }

        // Assuming the first content item contains the response text
        final String responseText = response.content().get(0).text();

        // Include additional information from the response
        return String.format("Response from %s (ID: %s):\n\n%s\n\nTokens used: %d input, %d output\nStop reason: %s\nStop sequence: %s",
                response.model(),
                response.id(),
                responseText,
                response.usage().inputTokens(),
                response.usage().outputTokens(),
                response.stopReason().getValue(),
                response.stopSequence().orElse("N/A"));
    }
}
