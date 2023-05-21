package webapp.config;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SshTunnel implements InitializingBean, DisposableBean {

    private Session session;

    @Value("${spring.ssh.username}")
    private String username;

    @Value("${spring.ssh.password}")
    private String password;

    public SshTunnel() {}

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(username, "server.domain", 22);
            session.setPassword(password);

            // Avoiding known_hosts file check
            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect();
            session.setPortForwardingL(1521, "server", 1521);

            System.out.println("SSH Tunnel created successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() throws Exception {
        if (session != null && session.isConnected()) {
            session.disconnect();
            System.out.println("SSH Tunnel closed successfully");
        }
    }
}