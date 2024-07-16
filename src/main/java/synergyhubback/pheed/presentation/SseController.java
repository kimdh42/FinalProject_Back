package synergyhubback.pheed.presentation;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.Writer;

@RestController
@RequestMapping("/sse")
public class SseController {

    @GetMapping
    public void sse(final HttpServletResponse response) throws IOException, InterruptedException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        Writer writer = response.getWriter();

        for (int i = 0; i < 20; i++) {
            writer.write("data: " + System.currentTimeMillis() + "\n\n");
            writer.flush(); // 꼭 flush 해주어야 한다.
            Thread.sleep(1000);
        }

        writer.close();
    }

}
