package com.ecommerce.praticboutic_backend_java.services;

import com.ecommerce.praticboutic_backend_java.entities.Commande;
import com.ecommerce.praticboutic_backend_java.entities.Customer;
import com.ecommerce.praticboutic_backend_java.entities.LigneCmd;
import com.ecommerce.praticboutic_backend_java.repositories.CommandeRepository;
import com.ecommerce.praticboutic_backend_java.repositories.CustomerRepository;
import com.ecommerce.praticboutic_backend_java.repositories.LigneCmdRepository;
import com.ecommerce.praticboutic_backend_java.repositories.StatutCmdRepository;
import com.ecommerce.praticboutic_backend_java.requests.Item;
import com.ecommerce.praticboutic_backend_java.utils.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.Instant.now;


@Service
public class DepartCommandeService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CommandeRepository commandeRepository;

    @Autowired
    private LigneCmdRepository ligneCmdRepository;

    @Autowired
    private StatutCmdRepository statutCmdRepository;

    // Déclarez le logger en tant que champ statique en haut de votre classe
    private static final Logger logger = LoggerFactory.getLogger(DepartCommandeService.class);

    @Value("${app.mail.from.address}")
    private String fromEmail;

    @Value("${app.mail.from.name}")
    private String fromName;



    public void sendEmail(String recipientEmail, String subject, String compteurCommande, Map<String, Object> input , Double[] sum, String token) throws MessagingException, UnsupportedEncodingException {
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(fromEmail, fromName);
        helper.setTo(recipientEmail);
        helper.setSubject(subject);

        String strContent = generateEmailContent(compteurCommande, input, sum, token);

        helper.setText(strContent, true);

        mailSender.send(message);
    }


    public String generateEmailContent(String compteurCommande, Map<String, Object> input, Double[] sum, String token) {
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        StringBuilder text = new StringBuilder();
        InputStream inputStream = EmailService.class.getClassLoader().getResourceAsStream("./static/logopbsvg.html");
        String logopb = "";
        if (inputStream != null) {
            try {
                logopb = new String(inputStream.readAllBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.error("InputStream est null !");
        }

        // Début du contenu HTML
        text.append("<!DOCTYPE html>");
        text.append("<html>");
        text.append("<head>");
        text.append("<meta charset=\"UTF-8\">");
        text.append("<link href='https://fonts.googleapis.com/css?family=Public+Sans' rel='stylesheet'>");
        text.append("</head>");
        text.append("<body>");
        text.append(logopb);
        text.append("<br><br>");
        text.append("<p style=\"font-family: 'Sans'\"><b>R&eacute;f&eacute;rence commande: </b> ").append(compteurCommande).append("<br></p>");
        text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");

        if (Integer.parseInt(payload.get("method").toString()) == 2) {
            text.append("<p style=\"font-family: 'Sans'\"><b>Vente : </b>Consomation sur place<br></p>");
            text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            text.append("<p style=\"font-family: 'Sans'\"><b>Commande table num&eacute;ro : </b> ").append(payload.get("table")).append("<br></p>");
            text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            text.append("<p style=\"font-family: 'Sans'\"><b>T&eacute;l&eacute;phone : </b>").append(input.get("telephone")).append("<br></p>");
            text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
        }

        if (Integer.parseInt(payload.get("method").toString()) == 3) {
            if (input.get("vente").equals("EMPORTER")) {
                text.append("<p style=\"font-family: 'Sans'\"><b>Vente : </b> A emporter<br></p>");
                text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            }
            if (input.get("vente").equals("LIVRER")) {
                text.append("<p style=\"font-family: 'Sans'\"><b>Vente : </b> A livrer<br></p>");
                text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            }
            if (input.get("paiement").equals("COMPTANT")) {
                text.append("<p style=\"font-family: 'Sans'\"><b>Paiement : </b> Au comptant<br></p>");
                text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            }
            if (input.get("paiement").equals("LIVRAISON")) {
                text.append("<p style=\"font-family: 'Sans'\"><b>Paiement : </b> A la livraison<br></p>");
                text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            }

            text.append("<p style=\"font-family: 'Sans'\"><b>Nom du client : </b>")
                    .append(Utils.sanitizeInput(input.get("nom").toString())).append(" ")
                    .append(Utils.sanitizeInput(input.get("prenom").toString()))
                    .append("<br></p>");
            text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            text.append("<p style=\"font-family: 'Sans'\"><b>T&eacute;l&eacute;phone : </b>").append(input.get("telephone").toString()).append("<br></p>");
            text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");

            if (input.get("vente").equals("LIVRER")) {
                text.append("<p style=\"font-family: 'Sans'\"><b>Adresse (ligne1) : </b>")
                        .append(Utils.sanitizeInput(input.get("adresse1").toString()))
                        .append("</p><hr style=\"width:50%;text-align:left;margin-left:0\">");
                text.append("<p style=\"font-family: 'Sans'\"><b>Adresse (ligne2) : </b>")
                        .append(Utils.sanitizeInput(input.get("adresse2").toString()))
                        .append("</p><hr style=\"width:50%;text-align:left;margin-left:0\">");
                text.append("<p style=\"font-family: 'Sans'\"><b>Code Postal : </b>")
                        .append(Utils.sanitizeInput(input.get("codepostal").toString()))
                        .append("<br><hr style=\"width:50%;text-align:left;margin-left:0\"></p>");
                text.append("<p style=\"font-family: 'Sans'\"><b>Ville : </b>")
                        .append(Utils.sanitizeInput(input.get("ville").toString()))
                        .append("<br><hr style=\"width:50%;text-align:left;margin-left:0\"></p>");
            }
        }

        text.append("<p style=\"font-family: 'Sans'\"><b>Information compl&eacute;mentaire : </b>");
        // Conversion nl2br et stripslashes
        String infoSup = input.get("infosup").toString()
                .replace("\n", "<br>")
                .replace("\\", "");
        infoSup = Utils.sanitizeInput(infoSup);
        text.append(infoSup).append("</p>");
        text.append("<hr style=\"border: 3px solid black;margin-top:15px;margin-bottom:25px;width:50%;text-align:left;margin-left:0\">");

        Integer val = 0;

        text.append("<p style=\"font-size:130%;margin-bottom:25px;font-family: 'Sans'\"><b>D&eacute;tail de la commande : </b><br></p>");

        //ArrayList<Item> items = (ArrayList<Item>) input.get("items");
        List<Map<String, Object>> itemsRaw = (List<Map<String, Object>>) input.get("items");

        ObjectMapper mapper = new ObjectMapper();
        List<Item> items = itemsRaw.stream()
                .map(map -> mapper.convertValue(map, Item.class))
                .collect(Collectors.toList());
        int numItems = items.size();
        int i = 0;

        for (Item item : items) {
            i++;

            text.append("<p style=\"font-family: 'Sans'\">");
            text.append("Ligne ").append(i).append("<br>");
            text.append("<b>").append(Utils.sanitizeInput(item.getName())).append("</b><br>");

            NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);

            text.append(item.getQt())
                    .append(" x ")
                    .append(formatter.format(item.getPrix()))
                    .append(Utils.sanitizeInput(item.getUnite()))
                    .append("<br>");

            text.append(item.getOpts());

            // Conversion nl2br et stripslashes pour txta
            String txta = item.getTxta()
                    .replace("\n", "<br>")
                    .replace("\\", "");
            txta = Utils.sanitizeInput(txta);

            text.append("<i>").append(txta).append("</i>");
            text.append("</p>");

            if (i != numItems) {
                text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            }

            val += item.getQt();
            sum[0] += item.getPrix() * item.getQt();
        }

        text.append("<hr style=\"border: 3px solid black;margin-top:15px;margin-bottom:25px;width:50%;text-align:left;margin-left:0\">");

        NumberFormat formatter = NumberFormat.getInstance(Locale.FRANCE);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        if (!input.get("vente").equals("LIVRER")) {
            text.append("<p style=\"font-size:130%;font-family: 'Sans'\">Remise : ")
                    .append(formatter.format(-Double.parseDouble(input.get("remise").toString())))
                    .append("€ <br></p>");
            text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            text.append("<p style=\"font-size:130%;font-family: 'Sans'\"><b>Total Commande : ")
                    .append(formatter.format(sum[0] - Double.parseDouble(input.get("remise").toString())))
                    .append("€ </b><br></p>");
        } else {
            text.append("<p style=\"font-size:130%;font-family: 'Sans'\">Sous-total Commande : ")
                    .append(formatter.format(sum[0]))
                    .append("€")
                    .append(" <br></p>");
            text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            text.append("<p style=\"font-size:130%;font-family: 'Sans'\">Remise : ")
                    .append(formatter.format(-Double.parseDouble(input.get("remise").toString())))
                    .append("€")
                    .append(" <br></p>");
            text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            text.append("<p style=\"font-size:130%;font-family: 'Sans'\">Frais de Livraison : ")
                    .append(formatter.format(Double.parseDouble(input.get("fraislivr").toString())))
                    .append(Utils.sanitizeInput("€"))
                    .append(" <br></p>");
            text.append("<hr style=\"width:50%;text-align:left;margin-left:0\">");
            text.append("<p style=\"font-size:130%;font-family: 'Sans'\"><b>Total Commande : ")
                    .append(formatter.format(sum[0] - Double.parseDouble(input.get("remise").toString()) + Double.parseDouble(input.get("fraislivr").toString())))
                    .append("€")
                    .append(" </b><br></p>");
        }

        text.append("</body>");
        text.append("</html>");

        return text.toString();

    }

    public Integer enregistreCommande(String compteurCommande, Map<String, Object> input, Double[] sum, String token)
    {
        Map <java.lang.String, java.lang.Object> payload = JwtService.parseToken(token).getClaims();

        // Enregistrer la commande dans la base de données
        Customer custo = customerRepository.findByCustomer(payload.get("customer").toString());

        Commande order = new Commande();
        order.setNumRef(compteurCommande);
        order.setCustomId(custo.getCustomId());

        // Récupérer les données du Map input
        // Assurez-vous que les clés correspondent à vos données d'entrée
        order.setNom(input.get("nom").toString());
        order.setPrenom(input.get("prenom").toString());
        order.setTelephone(input.get("telephone").toString());
        order.setAdresse1(input.get("adresse1").toString());
        order.setAdresse2(input.get("adresse2").toString());
        order.setCodePostal(input.get("codepostal").toString());
        order.setVille(input.get("ville").toString());
        order.setVente(input.get("vente").toString());
        order.setPaiement(input.get("paiement").toString());
        order.setSsTotal(sum[0]);
        order.setRemise( - Double.parseDouble(input.get("remise").toString()));
        order.setFraisLivraison(Double.parseDouble(input.get("fraislivr").toString()));
        order.setTotal(sum[0] - Double.parseDouble(input.get("remise").toString()) + Double.parseDouble(input.get("fraislivr").toString()));
        order.setCommentaire(input.get("infosup").toString());

        // Convertir la méthode de commande en chaîne de caractères
        String methodStr = switch (payload.get("method").toString()) {
            case "2" -> "ATABLE";
            case "3" -> "CLICKNCOLLECT";
            default -> "INCONNU";
        };
        order.setMethod(methodStr);

        // Convertir la valeur de la table en Integer
        String tableValue = payload.get("table").toString();
        if (tableValue != null && !tableValue.isEmpty()) {
            order.setTable(Integer.parseInt(tableValue));
        }

        order.setDateCreation(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        order.setStatId(statutCmdRepository.findByCustomidAndDefaut(custo.getCustomId(), 1).getStatid());

        // Sauvegarde de la commande
        commandeRepository.save(order);

        Integer cmdId = order.getCmdId();
        if (cmdId == null || cmdId == 0) {
            throw new RuntimeException("Erreur lors de la récupération de l'ID de la commande");
        }

        // Enregistrer les lignes de commande
        Integer ordre = 0;
        @SuppressWarnings("unchecked")
        //List<Item> items = (List<Item>) input.get("items");
        ObjectMapper mapper = new ObjectMapper();

        List<Map<String, Object>> rawItems = (List<Map<String, Object>>) input.get("items");
        List<Item> items = rawItems.stream()
                .map(map -> mapper.convertValue(map, Item.class))
                .collect(Collectors.toList());
        if (items != null) {
            for (Item item : items) {
                ordre++;
                enregistreLigneCmd(custo.getCustomId(), ordre, cmdId, item);
            }
        }

        return cmdId;
    }

    public void enregistreLigneCmd(Integer customId, Integer ordre, Integer cmdId, Item item)
    {
        // Initialisation des IDs pour l'article et l'option
        Integer artId = 0;
        Integer optId = 0;

        // Détermination de l'ID en fonction du type de l'item
        if ("article".equals(item.getType())) {
            artId = Integer.parseInt(item.getId());
        } else if ("option".equals(item.getType())) {
            optId = Integer.parseInt(item.getId());
        }

        // Création d'une nouvelle instance de LigneCmd
        LigneCmd ligneCmd = new LigneCmd();
        ligneCmd.setCustomId(customId); // customid doit être défini dans le contexte de la classe
        ligneCmd.setCmdId(cmdId);
        ligneCmd.setOrdre(ordre);
        ligneCmd.setType(item.getType());
        ligneCmd.setNom(item.getName());
        ligneCmd.setPrix(item.getPrix().floatValue()); // Conversion de Double à Float
        ligneCmd.setQuantite(item.getQt().floatValue()); // Conversion de Integer à Float
        ligneCmd.setCommentaire(item.getTxta());
        ligneCmd.setArtId(artId);
        ligneCmd.setOptId(optId);

        // Enregistrement de la ligne de commande dans la base de données
        // Ici, vous devez utiliser votre mécanisme de persistance (par exemple, un repository JPA)
        // Exemple avec un repository JPA :
         ligneCmdRepository.save(ligneCmd);
    }

}
