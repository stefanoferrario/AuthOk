package user;

import authorizer.MethodsUtils;
import jsonrpc.JSONRPCException;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainClass {
    private static Scanner scanner = new Scanner(System.in);
    private static final String USERNAME = "((\\_[a-zA-Z])(\\w+)?)|([a-zA-Z](\\w+)?)";
    private static final String COMMAND_NUM = "[1-7]";
    private static final String LEVEL = "[1-9]";
    private static final String DATE = "^(?:(?:31(\\/|-|\\.)(?:0?[13578]|1[02]|(?:Jan|Mar|May|Jul|Aug|Oct|Dec)))\\1|(?:(?:29|30)(\\/|-|\\.)(?:0?[1,3-9]|1[0-2]|(?:Jan|Mar|Apr|May|Jun|Jul|Aug|Sep|Oct|Nov|Dec))\\2))(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$|^(?:29(\\/|-|\\.)(?:0?2|(?:Feb))\\3(?:(?:(?:1[6-9]|[2-9]\\d)?(?:0[48]|[2468][048]|[13579][26])|(?:(?:16|[2468][048]|[3579][26])00))))$|^(?:0?[1-9]|1\\d|2[0-8])(\\/|-|\\.)(?:(?:0?[1-9]|(?:Jan|Feb|Mar|Apr|May|Jun|Jul|Aug|Sep))|(?:1[0-2]|(?:Oct|Nov|Dec)))\\4(?:(?:1[6-9]|[2-9]\\d)?\\d{2})$";
    //obbligare 4 cifre per l'anno


    private static CreatoreRichiesta  cr = new CreatoreRichiesta();
    private static Utente u;

    public static void main(String[] args) throws JSONRPCException {
        String name = getInput("Nome utente", USERNAME);
        u = new Utente(name);
        System.out.println("Utente creato");

        StringBuilder b = new StringBuilder();
        b.append(System.lineSeparator());
        b.append("1. Crea autorizzazione");
        b.append(System.lineSeparator());
        b.append("2. Revoca autorizzazione");
        b.append(System.lineSeparator());
        b.append("3. Verifica esistenza autorizzazione");
        b.append(System.lineSeparator());
        b.append("4. Crea token");
        b.append(System.lineSeparator());
        b.append("5. Verifica stato utente corrente"); //Permette di controllare lo stato dell'utente attuale, non manda richieste
        b.append(System.lineSeparator());
        b.append("6. Verifica stato server"); //Permette di controllare lo stato del server. Non è richiesto dalla specifica ma permette di testare il progetto più facilmente
        b.append(System.lineSeparator());
        b.append("7. Esci");
        boolean flag = true;
        while (flag) {
            System.out.println(b.toString());
            try {
                switch (Integer.parseInt(getInput("Comando", COMMAND_NUM))) {
                    case 1: createAuth(); break;
                    case 2: deleteAuth(); break;
                    case 3: checkAuth(); break;
                    case 4: createToken(); break;
                    case 5: checkUser(); break;
                    case 6: checkServer(); break;
                    case 7: flag = false; break;
                }
            } catch (AuthorizerException e) {
                System.out.println("Errore nell'esecuzione delle richiesta: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void createAuth() throws AuthorizerException {
        System.out.println("Creazione autorizzazione");
        String name = getInput("Nome utente", USERNAME);
        int level = Integer.parseInt(getInput("Livello", LEVEL));
        Date s = null;
        boolean flag = true;
        while (flag) {
            try {
                String date = getInput("Scadenza", DATE);;
                s = MethodsUtils.DATE_FORMAT.parse(date.replace('/', '-'));
                flag = false;
            } catch (ParseException e) {
                System.out.println("Formato data invalida");
            }
        }


        String existingKey = cr.verificaEsistenzaAutorizzazione(name);
        if (existingKey != null) {
            String ris = getInput("Autorizzazione già esistente: sovrascrivere l' autorizzazione attuale? (s/n)", "s|n");

            if (ris.equals("s")) {
                cr.revocaAutorizzazione(existingKey);
            } else {
                System.out.println("Autorizzazione non sovrascritta");
                return;
            }
        }

        String key = cr.creaAutorizzazione(name, level, s);
        System.out.println("Autorizzazione creata. Chiave assegnata all'utente " + name + ": " + key);
        if (u.getNome().equals(name)) {
            u.setChiave(key);
        }


    }
    private static void deleteAuth() throws AuthorizerException {
        String key = getInput("Chiave", USERNAME);
        if (cr.revocaAutorizzazione(key)) {
            System.out.println("Autorizzazione cancellata");
        } else {
            System.out.println("Autorizzazione già inesistente");
        }
    }
    private static void checkAuth() throws AuthorizerException {
        String user = getInput("Nome utente", USERNAME);
        String key = cr.verificaEsistenzaAutorizzazione(user);
        if (key == null) {
            System.out.println("Utente " + user + " NON autorizzato");
        } else {
            System.out.println("Utente " + user + " ha autorizzazione " + key);
        }

        //se esiste un'autorizzazione (key!=null) per l'utente corrente (u.nome equals user)
        //che non ha la chiave o ne ha una diversa (quindi non più valida)
        //viene aggiornata la chiave dell'utente corrente
        if (key != null && u.getNome().equals(user) && (!u.hasKey() || !u.getChiave().equals(key))) {
            u.setChiave(key);
            System.out.println("All'utente corrente " + user + "è stata assegnata l'autorizzazione");
        }
    }
    private static void createToken() throws AuthorizerException {
        int resource = Integer.parseInt(getInput("ID risorsa", LEVEL));
        if (u.hasKey()) {
            String token = cr.creaToken(u.getChiave(), resource);
            u.putToken(resource, token);
            System.out.println("Token ottenuto: " + token);
        } else {
            System.out.println("L'utente non ha nessuna autorizzazione");
        }
    }

    private static void checkUser() {
        StringBuilder b = new StringBuilder();
        b.append("Utente corrente: ");
        b.append(u.getNome());
        b.append(System.lineSeparator());
        if (u.hasKey()) {
            b.append("Chiave assegnata: ");
            b.append(u.getChiave());
        } else {
            b.append("Nessuna chiave assegnata");
        }
        b.append(System.lineSeparator());
        if (u.hasTokens()) {
            b.append("Token assegnati:");
            for (HashMap.Entry<Integer,String> token : u.getTokens().entrySet()) {
                b.append("TOKEN: ");
                b.append(token.getValue());
                b.append(" - RISORSA: ");
                b.append(token.getKey());
                b.append(System.lineSeparator());
            }
        } else {
            b.append("Nessun token assegnato");
        }
        System.out.println(b.toString());
    }

    private static void checkServer() {
        System.out.println(cr.checkServer());
    }

    private static String getInput(String varName, String expression) {
        String input = "";
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(input);
        while (!matcher.matches()) {
            System.out.println("Inserire "+ varName);
            input = scanner.nextLine();
            matcher = pattern.matcher(input);
            if (!matcher.matches()) {
                System.out.println("Input non valido");
            }
        }
        return input;
    }
}