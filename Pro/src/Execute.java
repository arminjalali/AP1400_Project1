import java.util.concurrent.*;

public class Execute {
    int chatOrVote;
    public Execute(int chatOrVote){
        this.chatOrVote = chatOrVote;
    }
    public String readLine() throws InterruptedException {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        String input = null;
        try {
            Future<String> result = ex.submit(new Input());
            try {
                if (chatOrVote == 1){
                    input = result.get(10 , TimeUnit.SECONDS);
                }
                else if (chatOrVote == 0){
                    input = result.get(10 , TimeUnit.SECONDS);}
            } catch (ExecutionException e) {
                e.getCause().printStackTrace();
            } catch (TimeoutException e) {
                result.cancel(true);
            }
        } finally {
            ex.shutdownNow();
        }
        return input;
    }
}
