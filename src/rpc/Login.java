package rpc;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import db.DBConnctionFactory;
import db.DBConnection;

/**
 * Servlet implementation class Login
 */
@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnctionFactory.getConnection();
		try {
			JSONObject object = new JSONObject();
			HttpSession session = request.getSession(false);
			if(session == null) {
				response.setStatus(403);
				object.put("status", "Session invalid");
			} else {
				String userId = (String)session.getAttribute("user_id");
				String name = conn.getFullname(userId);
				object.put("status", "OK");
				object.put("user_id", userId);
				object.put("name", name);
			}
			RpcHelper.WriteJsonObject(response, object);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBConnection conn = DBConnctionFactory.getConnection();
		try {
			JSONObject input = RpcHelper.readJsonObject(request);
			String userId = input.getString("user_id");
			String pwd = input.getString("password");
			JSONObject obj = new JSONObject();
			
			if(conn.verifyLogin(userId, pwd)) {
				HttpSession session = request.getSession();
				session.setAttribute("user_id", userId);
				
				// set session to expire in 10 minutes
				session.setMaxInactiveInterval(10 * 60);
				
				// get user name
				String name = conn.getFullname(userId);
				obj.put("status", "OK");
				obj.put("user_id", userId);
				obj.put("name", name);
			} else {
				response.setStatus(401);
			}
			RpcHelper.WriteJsonObject(response, obj);
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			conn.close();
		}
	}

}
