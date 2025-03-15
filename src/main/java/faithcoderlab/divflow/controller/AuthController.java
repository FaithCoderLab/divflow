package faithcoderlab.divflow.controller;

import faithcoderlab.divflow.dto.AuthResponse;
import faithcoderlab.divflow.dto.SignUpRequest;
import faithcoderlab.divflow.model.Member;
import faithcoderlab.divflow.service.MemberService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final MemberService memberService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUpRequest request) {
        log.debug("auth/signup -> username: {}", request.getUsername());

        Member member = memberService.registerMember(request);

        return ResponseEntity.ok(
                AuthResponse.builder()
                        .username(member.getUsername())
                        .token(null)
                        .build()
        );
    }
}
