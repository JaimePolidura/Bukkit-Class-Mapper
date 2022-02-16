package es.jaimetruman.commands.newversion;

import es.jaimetruman.commands.Command;

import java.lang.reflect.Field;

public class CommandArgsObjectBuilder {
    public <T> T build(Command commandInfo, String[] actualArgs, Class<T> classObjectArgs) throws Exception {
        try{
            return tryToBuild(commandInfo, actualArgs, classObjectArgs);
        }catch (Exception e) {
            throw new Exception("Incorrecto use: " + commandInfo.usage());
        }
    }

    private <T> T tryToBuild(Command commandInfo, String[] actualArgs, Class<T> classObjectArgs) throws Exception {
        String[] requiredArgs = commandInfo.args();
        T newInstance = classObjectArgs.newInstance();

        for (int i = 0; i < requiredArgs.length; i++) {
            String argName = requiredArgs[i];

            if(isOptional(argName) && i >= actualArgs.length) break;

            String actualArg = actualArgs[i];

            Field field = newInstance.getClass().getDeclaredField(argName);
            field.setAccessible(true);
            field.set(newInstance, parseStringToFieldType(field, actualArg));
        }

        return newInstance;
    }

    private static boolean isOptional(String arg){
        return arg.contains("[");
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
        }else if(fieldTypeName.equalsIgnoreCase("float") || fieldTypeName.equalsIgnoreCase("java.lang.Float")){
            return Float.parseFloat(arg);
        }

        return arg;
    }
}
