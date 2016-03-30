package edu.kpi.nesteruk.pzcs.view.localization;

/**
 * Created by Anatolii Bed on 24.03.2016.
 */
public class Localization {

    private static final Localization INSTANCE = new Localization();

    private Language language;

    private Localization() {
    }

    public static Localization getInstance() {
        return INSTANCE;
    }

    public void init(Language language) {
        this.language = language;
    }

    public Language getLanguage() {
        return language;
    }

    public enum Language {

        EN("PZCS-2 Editor", "Task Graph", "New", "Graph editor", "Graph generator", "Open", "Save",
                "System graph", "New system", "Modeling", "Processors params", "Gant diagram",
                "Statistics", "Help", "About", "Validate", "Exit"),
        UA("ПЗКС", "Граф задачі", "Новий", "Редактор графу", "Генератор графу", "Відкрити", "Зберегти в файл",
                "Граф системи", "Нова система", "Моделювання", "Параметри процесорів", "Діаграма Ганта",
                "Статистика", "Допомога", "Про", "Валідація", "Вихід");

        Language(String pzks, String taskGraph, String aNew, String graphEditor, String graphGenerator, String open,
                 String save, String systemGraph, String newSystem, String modeling, String processorsParams, String gantDiagram,
                 String statistics, String help, String about, String validate, String exit) {
            this.pzks = pzks;
            this.taskGraph = taskGraph;
            this.aNew = aNew;
            this.graphEditor = graphEditor;
            this.graphGenerator = graphGenerator;
            this.open = open;
            this.save = save;
            this.systemGraph = systemGraph;
            this.newSystem = newSystem;
            this.modeling = modeling;
            this.processorsParams = processorsParams;
            this.gantDiagram = gantDiagram;
            this.statistics = statistics;
            this.help = help;
            this.about = about;
            this.validate = validate;
            this.exit = exit;
        }

        public final String pzks;
        public final String taskGraph;
        public final String aNew;
        public final String graphEditor;
        public final String graphGenerator;
        public final String open;
        public final String save;
        public final String systemGraph;
        public final String newSystem;
        public final String modeling;
        public final String processorsParams;
        public final String gantDiagram;
        public final String statistics;
        public final String help;
        public final String about;
        public final String validate;
        public final String exit;

    }
}
