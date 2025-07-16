package com.kimsang.smsgateway.common.migration;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MigrationCommandLineRunner implements CommandLineRunner {
  private final Environment env;

  public MigrationCommandLineRunner(Environment env) {
    this.env = env;
  }

  @Override
  public void run(String... args) {
    // only run if `--migrate` is passed
    if (args.length > 0 && args[0].equalsIgnoreCase("--migrate")) {
      log.info("Running migration...");

      String jdbcUrl = env.getProperty("spring.flyway.url");
      String username = env.getProperty("spring.r2dbc.username");
      String password = env.getProperty("spring.r2dbc.password");
      String locations = env.getProperty("spring.flyway.locations", "classpath:db/migration");

      Flyway flyway = Flyway.configure()
          .dataSource(jdbcUrl, username, password)
          .locations(locations)
          .load();

      flyway.migrate();
      log.info("migration completed");
    } else {
      log.info("no `--migrate` flag passed. skipping migration.");
    }
  }
}
