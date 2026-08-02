package com.studio.api.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * PostgreSQL 접속 비밀번호가 실제로 주입됐는지 기동 시점에 확인한다.
 *
 * <p>{@code application.yml} 에는 비밀번호 기본값을 두지 않으므로(미주입 시 빈 값),
 * 주입을 잊으면 여기서 명확한 메시지와 함께 기동을 중단시킨다. 커밋된 설정에 남은
 * 비밀번호로 앱이 조용히 동작하는 상황을 막는 것이 목적이다.
 *
 * <p>Spring Boot 의 {@code @ConfigurationProperties} 바인더는 해석되지 않은
 * 플레이스홀더를 예외 없이 리터럴로 넘기기 때문에, {@code ${DB_PASSWORD}} 처럼
 * 기본값을 생략하는 것만으로는 기동이 중단되지 않는다. 그래서 이 검사가 필요하다.
 *
 * <p>테스트용 H2 등 PostgreSQL 이 아닌 데이터소스는 검사 대상이 아니다.
 */
@Component
public class DataSourceSecretCheck {

    private final String url;
    private final String password;

    public DataSourceSecretCheck(
            @Value("${spring.datasource.url:}") String url,
            @Value("${spring.datasource.password:}") String password) {
        this.url = url;
        this.password = password;
    }

    @PostConstruct
    void verifyPasswordProvided() {
        if (!url.startsWith("jdbc:postgresql")) {
            return;
        }
        if (!StringUtils.hasText(password)) {
            throw new IllegalStateException("""
                    DB_PASSWORD 가 설정되지 않았습니다. 보안상 설정 파일에 비밀번호 기본값을 두지 않으므로 \
                    실행 시 주입해야 합니다.
                      PowerShell : $env:DB_PASSWORD = "<비밀번호>"
                      bash       : export DB_PASSWORD='<비밀번호>'
                    또는 커밋되지 않는 application-local.yml 에 spring.datasource.password 를 설정한 뒤 \
                    --spring.profiles.active=local 로 실행하세요.""");
        }
    }
}
