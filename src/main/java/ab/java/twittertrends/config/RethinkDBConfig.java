package ab.java.twittertrends.config;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.net.Connection;

@Configuration
@PropertySource("classpath:config/rethinkdb.properties")
public class RethinkDBConfig {

	private static final String HOST = "rethink_host";
	private static final String PORT = "rethink_port";

	public static final String DB_NAME = "twitter_trends";

	public static final String PRIMARY_KEY_NAME = "primary_key";

	public static final String HASHTAG_TABLE_NAME = "hashtags";
	public static final String HASHTAG_PK_NAME = "name";

	@Autowired
	private Environment env;

	@PostConstruct
	public void initializeDatabase() {
		final Connection conn = createConnection();

		final List<String> databases = RethinkDB.r.dbList().run(conn);
		if (!databases.contains(DB_NAME)) {
			RethinkDB.r.dbCreate(DB_NAME).run(conn);
		}

		final List<String> tables = RethinkDB.r.db(DB_NAME).tableList().run(conn);
		if (!tables.contains(HASHTAG_TABLE_NAME)) {
			RethinkDB.r.db(DB_NAME).tableCreate(HASHTAG_TABLE_NAME).optArg(PRIMARY_KEY_NAME, HASHTAG_PK_NAME).run(conn);
		}
	}

	@Bean
	public Connection createConnection() {
		final String host = env.getRequiredProperty(HOST);
		final Integer port = env.getRequiredProperty(PORT, Integer.class);

		return RethinkDB.r.connection().hostname(host).port(port).connect();
	}
	
}