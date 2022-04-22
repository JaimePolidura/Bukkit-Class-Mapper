package es.jaimetruman.commands;

public final class BukkitUsageMessageBuilder {
    public String build(String command, String[] args, String helperCommand){
        String commandUsageWithoutArgs = String.format("/%s ", command);
        String[] normalizedArgs = this.normalizeCommandsArgs(args);

        String commnandUsage = hasArgs(args) ?
                commandUsageWithoutArgs + String.join(" ", normalizedArgs) :
                commandUsageWithoutArgs;

        return helperCommand.equals("") ?
                commnandUsage :
                String.format("%s %s", commnandUsage, "For more information: " + helperCommand);
    }

    private boolean hasArgs(String[] args){
        return !(args.length == 1 && args[0].equals(""));
    }

    private String[] normalizeCommandsArgs(String[] args) {
        String[] normalizedArgs = new String[args.length];

        for (int i = 0; i < args.length; i++) {
            String unnormalizedArg = args[i];

            normalizedArgs[i] = this.normalizeCommandArg(unnormalizedArg);
        }

        return normalizedArgs;
    }

    private String normalizeCommandArg(String unormlizedArg){
        if(isArgObligatory(unormlizedArg)){
            return String.format("<%s>", unormlizedArg);
        }else if(!isArgObligatory(unormlizedArg) && hasDefaultValue(unormlizedArg)){
            return unormlizedArg.substring(0, unormlizedArg.indexOf("¡"));
        }else{
            return unormlizedArg;
        }
    }

    private boolean isArgObligatory(String arg){
        return !arg.contains("[");
    }

    private boolean hasDefaultValue(String arg){
        return arg.contains("!");
    }
}
