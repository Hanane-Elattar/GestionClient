package pkg;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet("/Example")
public class Example extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    private String jdbcUrl = "jdbc:oracle:thin:@localhost:1521:XE";
    private String username = ""; // database username
    private String password = ""; // database password

    public Example() {
        super();
    }

    public void init(ServletConfig config) throws ServletException {
    }

    public void destroy() {
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        Connection conn = null;
        Statement stmt = null;
        Statement clientGarantStmt = null;
        ResultSet rs = null;
        ResultSet clientGarantRs = null;

        // Get feedback message from session, if any
        String feedback = (String) request.getSession().getAttribute("feedback");
        request.getSession().removeAttribute("feedback");

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, username, password);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM client");

            // Create a separate statement for clientGarant dropdown
            clientGarantStmt = conn.createStatement();
            clientGarantRs = clientGarantStmt.executeQuery("SELECT cin FROM client");

            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Gestion des Clients</title>");
            out.println("<meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            out.println("<style>");
            out.println("/* Reset et base */");
            out.println("* { box-sizing: border-box; margin: 0; padding: 0; }");
            out.println("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; line-height: 1.6; background: #f5f5f5; min-height: 100vh; padding: 20px; color: #333; }");

            out.println("/* Container principal */");
            out.println(".container { max-width: 1300px; margin: 0 auto; background: white; border-radius: 10px; padding: 25px; box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1); }");

            out.println("/* En-têtes */");
            out.println("h1 { text-align: center; margin-bottom: 30px; color: #2c3e50; font-weight: 600; font-size: 2em; padding-bottom: 15px; border-bottom: 2px solid #3498db; }");

            out.println("/* Section Ajout */");
            out.println(".add-section { background: #f8f9fa; padding: 25px; border-radius: 8px; margin-bottom: 30px; border: 1px solid #e1e8ed; }");
            out.println(".add-section h2 { color: #2c3e50; margin-bottom: 20px; font-size: 1.4em; }");
            out.println(".form-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 20px; margin-bottom: 20px; }");
            out.println(".form-group { display: flex; flex-direction: column; }");
            out.println(".form-group label { font-weight: 600; margin-bottom: 8px; color: #2c3e50; font-size: 0.95em; }");
            out.println(".form-group input, .form-group select { padding: 10px 12px; border: 1px solid #ddd; border-radius: 6px; font-size: 0.95em; transition: border-color 0.3s; }");
            out.println(".form-group input:focus, .form-group select:focus { border-color: #3498db; outline: none; box-shadow: 0 0 0 2px rgba(52, 152, 219, 0.2); }");
            out.println(".btn-add { background: #27ae60; color: white; padding: 12px 30px; border: none; border-radius: 6px; cursor: pointer; font-size: 1em; font-weight: 600; transition: background-color 0.3s; }");
            out.println(".btn-add:hover { background: #219a52; }");

            out.println("/* Tableau */");
            out.println(".table-section h2 { color: #2c3e50; margin-bottom: 20px; font-size: 1.4em; }");
            out.println(".table-container { overflow-x: auto; border-radius: 8px; border: 1px solid #e1e8ed; }");
            out.println("table { width: 100%; border-collapse: collapse; background: white; min-width: 1000px; }");
            out.println("th { background: #34495e; color: white; padding: 15px 12px; text-align: left; font-weight: 600; font-size: 0.9em; border-right: 1px solid #2c3e50; }");
            out.println("th:last-child { border-right: none; }");
            out.println("td { padding: 12px; border-bottom: 1px solid #e1e8ed; font-size: 0.9em; }");
            out.println("tr:hover { background-color: #f8f9fa; }");
            out.println("tr:nth-child(even) { background-color: #f8f9fa; }");

            out.println("/* Boutons d'action */");
            out.println(".action-buttons { display: flex; gap: 8px; }");
            out.println(".btn-edit { background: #3498db; color: white; padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 0.85em; transition: background-color 0.3s; }");
            out.println(".btn-edit:hover { background: #2980b9; }");
            out.println(".btn-delete { background: #e74c3c; color: white; padding: 6px 12px; border: none; border-radius: 4px; cursor: pointer; font-size: 0.85em; transition: background-color 0.3s; }");
            out.println(".btn-delete:hover { background: #c0392b; }");

            out.println("/* Formulaire de modification inline */");
            out.println(".edit-form { display: flex; flex-direction: column; gap: 10px; background: #f8f9fa; padding: 15px; border-radius: 6px; margin-top: 10px; border: 1px solid #e1e8ed; }");
            out.println(".edit-form .form-row { display: grid; grid-template-columns: repeat(auto-fit, minmax(150px, 1fr)); gap: 10px; }");
            out.println(".edit-form input, .edit-form select { padding: 8px 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 0.85em; }");
            out.println(".btn-update { background: #27ae60; color: white; padding: 8px 16px; border: none; border-radius: 4px; cursor: pointer; font-size: 0.85em; align-self: flex-start; }");
            out.println(".btn-update:hover { background: #219a52; }");

            out.println("/* Messages de feedback */");
            out.println(".feedback { background: #d4edda; color: #155724; padding: 12px 15px; border-radius: 6px; margin: 15px 0; border: 1px solid #c3e6cb; font-weight: 500; }");
            out.println(".error { background: #f8d7da; color: #721c24; padding: 12px 15px; border-radius: 6px; margin: 15px 0; border: 1px solid #f5c6cb; font-weight: 500; }");

            out.println("/* Responsive */");
            out.println("@media (max-width: 768px) {");
            out.println("    body { padding: 10px; }");
            out.println("    .container { padding: 15px; }");
            out.println("    .form-grid { grid-template-columns: 1fr; }");
            out.println("    .add-section { padding: 15px; }");
            out.println("    th, td { padding: 8px 6px; font-size: 0.85em; }");
            out.println("}");
            out.println("</style>");
            out.println("</head>");
            out.println("<body>");
            
            out.println("<div class='container'>");
            
            // Display feedback message, if any
            if (feedback != null) {
                out.println("<div class='" + (feedback.contains("Erreur") ? "error" : "feedback") + "'>" + feedback + "</div>");
            }

            // Section d'ajout - Style similaire à l'image WhatsApp
            out.println("<div class='add-section'>");
            out.println("<h2>Ajouter un nouveau Client</h2>");
            out.println("<form method='post' action='Example'>");
            out.println("<input type='hidden' name='action' value='create'>");
            out.println("<div class='form-grid'>");
            out.println("<div class='form-group'>");
            out.println("<label for='cin'>CIN</label>");
            out.println("<input type='text' id='cin' name='cin' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label for='nom'>NOM</label>");
            out.println("<input type='text' id='nom' name='nom' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label for='prenom'>Prénom</label>");
            out.println("<input type='text' id='prenom' name='prenom' required>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label for='telephone'>Téléphone</label>");
            out.println("<input type='text' id='telephone' name='telephone'>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label for='dateNaissance'>Date Naissance (YYYY-MM-DD)</label>");
            out.println("<input type='text' id='dateNaissance' name='dateNaissance'>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label for='villeOrigine'>Ville</label>");
            out.println("<input type='text' id='villeOrigine' name='villeOrigine'>");
            out.println("</div>");
            out.println("<div class='form-group'>");
            out.println("<label for='clientGarant'>Client Garant</label>");
            out.println("<select id='clientGarant' name='clientGarant'>");
            out.println("<option value=''>Aucun</option>");
            while (clientGarantRs.next()) {
                String cinOption = clientGarantRs.getString("cin");
                out.println("<option value='" + cinOption + "'>" + cinOption + "</option>");
            }
            out.println("</select>");
            out.println("</div>");
            out.println("</div>");
            out.println("<button type='submit' class='btn-add'>Ajouter</button>");
            out.println("</form>");
            out.println("</div>"); // Fin de add-section

            // Close the first clientGarantRs
            try { if (clientGarantRs != null) clientGarantRs.close(); } catch (Exception e) { /* Ignorer */ }
            try { if (clientGarantStmt != null) clientGarantStmt.close(); } catch (Exception e) { /* Ignorer */ }

            // Section tableau - Style similaire à l'image CRUD
            out.println("<div class='table-section'>");
            out.println("<h2>Liste des Clients</h2>");
            out.println("<div class='table-container'>");
            out.println("<table>");
            out.println("<thead>");
            out.println("<tr>");
            out.println("<th>CIN</th>");
            out.println("<th>Nom</th>");
            out.println("<th>Prénom</th>");
            out.println("<th>Téléphone</th>");
            out.println("<th>Date Naissance</th>");
            out.println("<th>Ville</th>");
            out.println("<th>Client Garant</th>");
            out.println("<th>Actions</th>");
            out.println("</tr>");
            out.println("</thead>");
            out.println("<tbody>");
            
            while (rs.next()) {
                String cin = rs.getString("cin");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String telephone = rs.getString("telephone");
                Date dateNaissance = rs.getDate("dateNaissance");
                String villeOrigine = rs.getString("villeOrigine");
                String clientGarant = rs.getString("clientGarant");
                
                out.println("<tr>");
                out.println("<td>" + cin + "</td>");
                out.println("<td>" + nom + "</td>");
                out.println("<td>" + prenom + "</td>");
                out.println("<td>" + (telephone != null ? telephone : "") + "</td>");
                out.println("<td>" + (dateNaissance != null ? dateNaissance : "") + "</td>");
                out.println("<td>" + (villeOrigine != null ? villeOrigine : "") + "</td>");
                out.println("<td>" + (clientGarant != null ? clientGarant : "") + "</td>");
                out.println("<td>");
                out.println("<div class='action-buttons'>");
                out.println("<form method='post' action='Example' style='display:inline;'>");
                out.println("<input type='hidden' name='action' value='delete'>");
                out.println("<input type='hidden' name='cin' value='" + cin + "'>");
                out.println("<button type='submit' class='btn-delete'>Supprimer</button>");
                out.println("</form>");
                out.println("<button type='button' class='btn-edit' onclick=\"toggleEditForm('" + cin + "')\">Modifier</button>");
                out.println("</div>");
                
                // Formulaire de modification caché
                out.println("<div id='edit-form-" + cin + "' class='edit-form' style='display: none;'>");
                out.println("<form method='post' action='Example'>");
                out.println("<input type='hidden' name='action' value='update'>");
                out.println("<input type='hidden' name='cin' value='" + cin + "'>");
                out.println("<div class='form-row'>");
                out.println("<input type='text' name='nom' value='" + nom + "' placeholder='Nom' required>");
                out.println("<input type='text' name='prenom' value='" + prenom + "' placeholder='Prénom' required>");
                out.println("<input type='text' name='telephone' value='" + (telephone != null ? telephone : "") + "' placeholder='Téléphone'>");
                out.println("<input type='text' name='dateNaissance' value='" + (dateNaissance != null ? dateNaissance : "") + "' placeholder='Date Naissance'>");
                out.println("<input type='text' name='villeOrigine' value='" + (villeOrigine != null ? villeOrigine : "") + "' placeholder='Ville'>");
                out.println("</div>");
                out.println("<div class='form-row'>");
                out.println("<select name='clientGarant'>");
                out.println("<option value=''>Aucun garant</option>");
                // Create a new statement and result set for each row's dropdown
                clientGarantStmt = conn.createStatement();
                clientGarantRs = clientGarantStmt.executeQuery("SELECT cin FROM client WHERE cin != '" + cin + "'");
                while (clientGarantRs.next()) {
                    String cinOption = clientGarantRs.getString("cin");
                    out.println("<option value='" + cinOption + "'" + (cinOption.equals(clientGarant) ? " selected" : "") + ">" + cinOption + "</option>");
                }
                out.println("</select>");
                out.println("<button type='submit' class='btn-update'>Mettre à jour</button>");
                out.println("</div>");
                out.println("</form>");
                out.println("</div>");
                out.println("</td>");
                out.println("</tr>");
                // Close the result set and statement for this row
                try { if (clientGarantRs != null) clientGarantRs.close(); } catch (Exception e) { /* Ignorer */ }
                try { if (clientGarantStmt != null) clientGarantStmt.close(); } catch (Exception e) { /* Ignorer */ }
            }
            out.println("</tbody>");
            out.println("</table>");
            out.println("</div>"); // Fin de table-container
            out.println("</div>"); // Fin de table-section
            
            out.println("</div>"); // Fin de container

            // JavaScript pour gérer l'affichage des formulaires de modification
            out.println("<script>");
            out.println("function toggleEditForm(cin) {");
            out.println("    var form = document.getElementById('edit-form-' + cin);");
            out.println("    if (form.style.display === 'none') {");
            out.println("        form.style.display = 'block';");
            out.println("    } else {");
            out.println("        form.style.display = 'none';");
            out.println("    }");
            out.println("}");
            out.println("</script>");

            out.println("</body>");
            out.println("</html>");

        } catch (ClassNotFoundException e) {
            out.println("<p class='error'>Erreur JDBC : driver non trouvé</p>");
            e.printStackTrace(out);
        } catch (SQLException e) {
            out.println("<p class='error'>Erreur SQL : " + e.getMessage() + "</p>");
            e.printStackTrace(out);
        } finally {
            try { if (clientGarantRs != null) clientGarantRs.close(); } catch (Exception e) { /* Ignorer */ }
            try { if (clientGarantStmt != null) clientGarantStmt.close(); } catch (Exception e) { /* Ignorer */ }
            try { if (rs != null) rs.close(); } catch (Exception e) { /* Ignorer */ }
            try { if (stmt != null) stmt.close(); } catch (Exception e) { /* Ignorer */ }
            try { if (conn != null) conn.close(); } catch (Exception e) { /* Ignorer */ }
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("action");
        Connection conn = null;
        PreparedStatement pstmt = null;
        String feedback = null;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            conn = DriverManager.getConnection(jdbcUrl, username, password);

            if ("create".equals(action)) {
                String sql = "INSERT INTO client (cin, nom, prenom, telephone, dateNaissance, villeOrigine, clientGarant) VALUES (?, ?, ?, ?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, request.getParameter("cin"));
                pstmt.setString(2, request.getParameter("nom"));
                pstmt.setString(3, request.getParameter("prenom"));
                pstmt.setString(4, request.getParameter("telephone"));
                String dateNaissance = request.getParameter("dateNaissance");
                if (dateNaissance != null && !dateNaissance.isEmpty()) {
                    pstmt.setDate(5, Date.valueOf(dateNaissance));
                } else {
                    pstmt.setNull(5, java.sql.Types.DATE);
                }
                pstmt.setString(6, request.getParameter("villeOrigine"));
                String clientGarant = request.getParameter("clientGarant");
                pstmt.setString(7, clientGarant != null && !clientGarant.isEmpty() ? clientGarant : null);
                pstmt.executeUpdate();
                feedback = "Client ajouté avec succès";
            } else if ("update".equals(action)) {
                String sql = "UPDATE client SET nom = ?, prenom = ?, telephone = ?, dateNaissance = ?, villeOrigine = ?, clientGarant = ? WHERE cin = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, request.getParameter("nom"));
                pstmt.setString(2, request.getParameter("prenom"));
                pstmt.setString(3, request.getParameter("telephone"));
                String dateNaissance = request.getParameter("dateNaissance");
                if (dateNaissance != null && !dateNaissance.isEmpty()) {
                    pstmt.setDate(4, Date.valueOf(dateNaissance));
                } else {
                    pstmt.setNull(4, java.sql.Types.DATE);
                }
                pstmt.setString(5, request.getParameter("villeOrigine"));
                String clientGarant = request.getParameter("clientGarant");
                pstmt.setString(6, clientGarant != null && !clientGarant.isEmpty() ? clientGarant : null);
                pstmt.setString(7, request.getParameter("cin"));
                pstmt.executeUpdate();
                feedback = "Client modifié avec succès";
            } else if ("delete".equals(action)) {
                String sql = "DELETE FROM client WHERE cin = ?";
                pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, request.getParameter("cin"));
                pstmt.executeUpdate();
                feedback = "Client supprimé avec succès";
            }

        } catch (ClassNotFoundException e) {
            feedback = "Erreur JDBC : driver non trouvé";
        } catch (SQLException e) {
            if (e.getErrorCode() == 2291) {
                feedback = "Erreur : Le client garant spécifié n'existe pas.";
            } else {
                feedback = "Erreur SQL : " + e.getMessage();
            }
        } finally {
            try { if (pstmt != null) pstmt.close(); } catch (Exception e) { /* Ignorer */ }
            try { if (conn != null) conn.close(); } catch (Exception e) { /* Ignorer */ }
        }

        // Store feedback in session to display after redirect
        if (feedback != null) {
            request.getSession().setAttribute("feedback", feedback);
        }

        // Redirect to refresh the page and update the table
        response.sendRedirect("Example");
    }
}