package com.allen.nw;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.cloud.sample.persistence.PersistenceWithJDBCServlet;
import com.sap.cloud.sample.persistence.Person;
import com.sap.cloud.sample.persistence.PersonDAO;
import com.sap.security.core.server.csi.IXSSEncoder;
import com.sap.security.core.server.csi.XSSEncoder;

/**
 * Servlet implementation class PersistenceWithNW
 */
public class PersistenceWithNW extends HttpServlet {
	 private static final long serialVersionUID = 1L;
	 private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceWithJDBCServlet.class);

	 private NWDAO nwDAO;

	 /** {@inheritDoc} */
    @Override
    public void init() throws ServletException {
        try {
            InitialContext ctx = new InitialContext();
            DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
            nwDAO = new NWDAO(ds);
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NamingException e) {
            throw new ServletException(e);
        }
    }
	    
    /** {@inheritDoc} */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("<p>Persistence with JDBC NW table!</p>");
        try {
        	displayNWTable(response);
//            appendAddForm(response);
        } catch (Exception e) {
            response.getWriter().println("Persistence operation failed with reason: " + e.getMessage());
            LOGGER.error("Persistence operation failed", e);
        }
    }   
    
    /** {@inheritDoc} */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
    	
    	doGet(request, response);
//        try {
//            doAdd(request);
//            doGet(request, response);
//        } catch (Exception e) {
//            response.getWriter().println("Persistence operation failed with reason: " + e.getMessage());
//            LOGGER.error("Persistence operation failed", e);
//        }
    }
    
    private void displayNWTable(HttpServletResponse response) throws SQLException, IOException {
        // Append table that lists all persons
        List<NW> resultList = nwDAO.selectAllEntries();
        response.getWriter().println(
                "<p><table width=70% border=\"1\"><tr><th colspan=\"4\">" + (resultList.isEmpty() ? "" : resultList.size() + " ")
                        + "Entries in the Database</th>"
                        + "<th colspan=\"2\">" + "Smart Sorted</th></tr>");
        if (resultList.isEmpty()) {
            response.getWriter().println("<tr><td colspan=\"4\">Database is empty</td></tr>");
        } else {
            response.getWriter().println("<tr><th>First name</th><th>Last name</th><th>Increase</th><th>Decrease</th><th>Amount</th><th>Total</th></tr>");
        }
        IXSSEncoder xssEncoder = XSSEncoder.getInstance();
        for (NW nw : resultList) {
        	response.getWriter().println(
                    "<tr><td height=\"30\"><center>" + xssEncoder.encodeHTML(nw.getFirstName()) + "</center></td>"
					+ "<td height=\"30\"><center>" + xssEncoder.encodeHTML(nw.getLastName()) + "</center></td>"
					+ "<td>" + "<center><a style=\"color:blue\" href=\"https://www.w3schools.com/html/\">Add</a></center>" + "</td>"
					+ "<td>" + "<center><a style=\"color:blue\" href=\"https://www.w3schools.com/html/\">Delete</a></center>" + "</td>"
					+ "<td height=\"30\"><center>" + nw.getAmount() + "</center></td>" // need to change to xssEncoder for getAmount()?
					+ "<td height=\"30\"><center>" + nw.getTotal() + "</center></td>" // need to change to xssEncoder for getTotal()?
					+ "</tr>");
        }
        response.getWriter().println("</table></p>");
    }

}
