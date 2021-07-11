import java.util.concurrent.*;

public class Execute {


    public String readLine() throws InterruptedException {
        ExecutorService ex = Executors.newSingleThreadExecutor();
        String input = null;
        try {
                Future<String> result = ex.submit(new Input());
                try {
                    input = result.get(10 , TimeUnit.SECONDS);
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
