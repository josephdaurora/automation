package daurora.automation;
import lombok.*;


@NoArgsConstructor
@Setter
@Getter
public class sqlCode {
    private String codetoExecute;
    private int numThreads;
    private String buildName;
    private int queueSize;
    private int timeoutTime;
}
