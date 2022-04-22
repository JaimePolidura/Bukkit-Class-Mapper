package es.jaimetruman.commands;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandArgsObjectBuilder {
    public <T> T build(CommandData commandData, String[] actualArgs, Class<T> classObjectArgs) throws Exception {
        return tryToBuild(commandData, actualArgs, classObjectArgs);
    }

    private <T> T tryToBuild(CommandData commandData, String[] actualArgs, Class<T> classObjectArgs) throws Exception {
        String[] requiredArgs = commandData.getArgs();
        T newInstance = classObjectArgs.newInstance();
        StringBuilder stringBuilderText = new StringBuilder();
        int requiredArgIndex = 0;
        List<String> argumensAdded = new ArrayList<>();

        for (int actualArgIndex = 0; actualArgIndex < actualArgs.length; actualArgIndex++) {
            String requiredArgUnnormalized = requiredArgs[requiredArgIndex];
            String requiredArgNameNormalized = normalizeArgName(requiredArgUnnormalized);
            String argValue = actualArgs[actualArgIndex];

            AnnalizyngArgState currentState = getCurrentState(actualArgIndex, requiredArgUnnormalized, actualArgs);

            switch (currentState){
                case NORMAL:
                    setField(newInstance, requiredArgNameNormalized, argValue);
                    argumensAdded.add(requiredArgUnnormalized);
                    requiredArgIndex++;
                    break;
                case TEXT:
                    stringBuilderText.append(argValue).append(" ");
                    break;
                case TEXT_END:
                    stringBuilderText.append(argValue);
                    setField(newInstance, requiredArgNameNormalized, stringBuilderText.toString());
                    argumensAdded.add(requiredArgUnnormalized);
                    break;
            }
        }

        checkIfArgumentMissingOrThrowException(argumensAdded, commandData.getArgs(), newInstance);

        return newInstance;
    }

    private String normalizeArgName(String arg){
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < arg.length(); i++)
            if(arg.charAt(i) != '.')
                stringBuilder.append(arg.charAt(i));

        String toReturn = stringBuilder.toString().contains("[") ?
                StringUtils.substringBetween(stringBuilder.toString(), "[", "]") :
                stringBuilder.toString();

        return toReturn.contains("¡") ?
                toReturn.substring(0, toReturn.indexOf("¡")) :
                toReturn;
    }

    private <T> void setField(T newInstance, String argName, String actualArg) throws Exception {
        Field field = newInstance.getClass().getDeclaredField(argName);
        field.setAccessible(true);
        field.set(newInstance, parseStringToFieldType(field, actualArg));
    }

    private static Object parseStringToFieldType(Field field, String arg){
        String fieldTypeName = field.getType().getName();

        if(fieldTypeName.equalsIgnoreCase("double") || fieldTypeName.equalsIgnoreCase("java.lang.Double")){
            return Double.parseDouble(arg);
        }else if(fieldTypeName.equalsIgnoreCase("int") || fieldTypeName.equalsIgnoreCase("java.lang.Integer")){
            return Integer.parseInt(arg);
        }else if(fieldTypeName.equalsIgnoreCase("boolean") || fieldTypeName.equalsIgnoreCase("java.lang.Boolean")){
            return Boolean.parseBoolean(arg);
        }else if(fieldTypeName.equalsIgnoreCase("short") || fieldTypeName.equalsIgnoreCase("java.lang.Short")){
            return Short.parseShort(arg);
        }else if(fieldTypeName.equalsIgnoreCase("long") || fieldTypeName.equalsIgnoreCase("java.lang.Long")){
            return Long.parseLong(arg);
        }else if(fieldTypeName.equalsIgnoreCase("float") || fieldTypeName.equalsIgnoreCase("java.lang.Float")) {
            return Float.parseFloat(arg);
        }else if(fieldTypeName.equalsIgnoreCase("java.lang.String")){
            return String.valueOf(arg);
        }else if(fieldTypeName.equalsIgnoreCase("java.util.UUID")){
            return UUID.fromString(String.valueOf(arg));
        }

        return field;
    }

    private AnnalizyngArgState getCurrentState(int indexOfActualArgs, String requiredArg, String[] actualArgs){
        if(isLongText(requiredArg) && indexOfActualArgs + 1 >= actualArgs.length){
            return AnnalizyngArgState.TEXT_END;
        }else if(isLongText(requiredArg)){
            return AnnalizyngArgState.TEXT;
        }else{
            return AnnalizyngArgState.NORMAL;
        }
    }

    private boolean isLongText(String reqArg){
        return reqArg.endsWith("...");
    }

    private void checkIfArgumentMissingOrThrowException(List<String> argumentsAdded, String[] unnormalizedRequiredArguments, Object instance) throws Exception {
        for (String unnormalizedRequiredArgument : unnormalizedRequiredArguments) {
            if(!argumentsAdded.contains(unnormalizedRequiredArgument) && isRequired(unnormalizedRequiredArgument)){
                throw new IllegalArgumentException();
            }else if(!argumentsAdded.contains(unnormalizedRequiredArgument) && hasDefaultValue(unnormalizedRequiredArgument)){
                setField(instance, normalizeArgName(unnormalizedRequiredArgument), getDefaultValue(unnormalizedRequiredArgument));
            }
        }
    }

    private String getDefaultValue(String arg){
        return StringUtils.substringBetween(arg, "¡", "!");
    }

    private boolean hasDefaultValue(String arg){
        return arg.contains("!");
    }

    private boolean isRequired(String arg){
        return !arg.contains("[");
    }

    private enum AnnalizyngArgState {
        NORMAL, TEXT, TEXT_END;
    }
}
