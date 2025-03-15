package faithcoderlab.divflow.service;

import faithcoderlab.divflow.dto.SignUpRequest;
import faithcoderlab.divflow.exception.CustomException;
import faithcoderlab.divflow.exception.ErrorCode;
import faithcoderlab.divflow.model.Member;
import faithcoderlab.divflow.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member registerMember(SignUpRequest request) {
        if (memberRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }

        String encodedPassword = passwordEncoder.encode(request.getPassword());

        Member member = Member.builder()
                .username(request.getUsername())
                .password(encodedPassword)
                .role(Member.Role.ROLE_USER)
                .build();

        return memberRepository.save(member);
    }

    public Member authenticate(String username, String password) {
        Member member = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(password, member.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_CREDENTIALS);
        }

        return member;
    }
}
