package letshangllc.stretchingroutinespro.helpers;

import android.app.Activity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Applies system bar insets so content is not drawn under the status/nav bars on Android 15+.
 */
public final class EdgeToEdgeHelper {
    private EdgeToEdgeHelper() {
    }

    public static void apply(@NonNull Activity activity, @NonNull View root) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(insets.left, insets.top, insets.right, insets.bottom);
            return windowInsets;
        });
        ViewCompat.requestApplyInsets(root);
    }
}
