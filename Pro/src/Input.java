import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

public class Input implements Callable<String> {
    @Override
    public String call() throws Exception {
        BufferedReader get = new BufferedReader(new InputStreamReader(System.in));
        String input;
        do {
            try {
                while (!get.ready()) {
                    Thread.sleep(200);
                }
                input = get.readLine();
            } catch (InterruptedException e) {
                System.out.println("Time is over!");
                return null;
            }
        } while ("".equals(input));
        return input;
    }
}
