package example;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import javax.ejb.EJB;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import example.ejb.WhoAmIBeanRemote;

/**
 * @author Jan Martiska
 */
@WebServlet(urlPatterns = "/")

public class ClientServlet extends HttpServlet {

   /* @EJB(lookup = "ejb:/server-side/WhoAmIBean!example.ejb.WhoAmIBeanRemote")
    private WhoAmIBeanRemote remoteBeanByInjection;
*/
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        resp.getWriter().append("Servlet running as user: " + req.getRemoteUser() +"\n");

        final String hostname = System.getProperty("remote.ejb.host");
        if (hostname == null) {
            resp.getWriter().append("ERROR: please specify remote.ejb.host property");
            return;
        }

       /* // invoke bean obtained by injection (@EJB)
        try {
            String username = callBeanByInjection();
            resp.getWriter().append("WhoAmI from bean obtained by injection returned: ").append(username).append("\n");
        } catch (Exception e) {
            resp.getWriter().append("Calling bean obtained by injection failed: ");
            e.printStackTrace(resp.getWriter());
        }

        // invoke bean obtained by JNDI lookup
        try {
            String username = callBeanByLookup();
            resp.getWriter().append("WhoAmI from bean obtained by lookup returned: ").append(username).append("\n");
        } catch (Exception e) {
            resp.getWriter().append("Calling bean obtained by lookup failed: ");
            e.printStackTrace(resp.getWriter());
        }*/

        // invoke bean obtained by scoped context
        try {
            String username = callBeanByScopedContext();
            resp.getWriter().append("WhoAmI from bean obtained by scoped context returned: ").append(username).append("\n");
        } catch(Exception e) {
            resp.getWriter().append("Calling bean obtained by scoped context failed: ");
            e.printStackTrace(resp.getWriter());
        }
    }

    private String callBeanByScopedContext() throws NamingException {
        Properties props = new Properties();
        props.put("remote.connections", "main");
        props.put("remote.connection.main.host", System.getProperty("remote.ejb.host"));
        props.put("remote.connection.main.port", "8080");
        props.put("remote.connection.main.username", "admin");
        props.put("remote.connection.main.password", "admin123+");
        InitialContext ctx =  null;
        try {
            ctx = new InitialContext(props);
            WhoAmIBeanRemote remoteBeanByLookup = (WhoAmIBeanRemote)ctx
                    .lookup("ejb:/server-side/WhoAmIBean!example.ejb.WhoAmIBeanRemote");
            return remoteBeanByLookup.whoAmI();
        } finally {
            if (ctx != null) {
                ((Context)ctx.lookup("ejb:")).close();
                ctx.close();
            }
        }

    }

   /* public String callBeanByInjection() throws IOException {
        return remoteBeanByInjection.whoAmI();

    }

    public String callBeanByLookup() throws NamingException, IOException {
        Properties props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "org.wildfly.naming.client.WildFlyInitialContextFactory");

        InitialContext ctx = new InitialContext(props);
        WhoAmIBeanRemote remoteBeanByLookup = (WhoAmIBeanRemote)ctx
                .lookup("ejb:/server-side/WhoAmIBean!example.ejb.WhoAmIBeanRemote");
        return remoteBeanByLookup.whoAmI();
    }*/
}