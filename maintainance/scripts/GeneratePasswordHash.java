import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Utility to generate BCrypt password hash
 * Run this with: java -cp "path/to/spring-security-crypto.jar" GeneratePasswordHash
 * Or compile and run in your IDE
 */
public class GeneratePasswordHash {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "12345";
        String hash = encoder.encode(password);
        
        System.out.println("==========================================");
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hash);
        System.out.println("==========================================");
        System.out.println("\nSQL INSERT statement:");
        System.out.println("INSERT INTO users (name, email, phone, password_hash, role, status, company_id, created_at)");
        System.out.println("VALUES (");
        System.out.println("    'Haitham Soliman',");
        System.out.println("    'haitham.soliman94@gmail.com',");
        System.out.println("    '1234567890',");
        System.out.println("    '" + hash + "',");
        System.out.println("    'OPERATION',");
        System.out.println("    'ACTIVE',");
        System.out.println("    (SELECT id FROM companies LIMIT 1),");
        System.out.println("    NOW()");
        System.out.println(");");
    }
}





