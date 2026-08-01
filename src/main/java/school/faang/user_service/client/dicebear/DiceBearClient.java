package school.faang.user_service.client.dicebear;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "dicebear-client",
        url = "${dicebear.api.url:https://api.dicebear.com/10.x}"
)
public interface DiceBearClient {
    @GetMapping("/{style}/png")
    Response getRandomAvatar(
            @PathVariable("style") String style,
            @RequestParam("seed") String seed);
}