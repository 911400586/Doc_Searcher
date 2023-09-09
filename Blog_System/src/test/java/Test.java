import com.example.blog_system.Common.StringTools;

import java.util.UUID;

public class Test {
    public static void main(String[] args) {
        String salt = UUID.randomUUID().toString().replace("-", "");
        System.out.println(salt);

    }
}
