package amplifyframework.datastore.generated.model;

import com.amplifyframework.util.Immutable;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.ModelProvider;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class AmplifyModelProvider implements ModelProvider{
    private static final String AMPLIFY_MODEL_VERSION = "3e784d836041ae86fcaaee5e79270b86";
    private static AmplifyModelProvider amplifyGeneratedModelInstance;
    private AmplifyModelProvider(){}


    public static AmplifyModelProvider getInstance(){
        if (amplifyGeneratedModelInstance == null){
            amplifyGeneratedModelInstance = new AmplifyModelProvider();
        }
        return amplifyGeneratedModelInstance;
    }


   @Override
    public Set<Class<?extends Model>> models(){
        final Set<Class<? extends Model>> modifiableSet = new HashSet<>(Array.<Class<? extends Model>> asList(Task.class , Team.class));
        return Immutable.of(modifiableSet);
    }


    @Override
    public String version() {
        return AMPLIFY_MODEL_VERSION;
    }
}
