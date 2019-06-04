package tp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jdom2.Attribute;
import org.jdom2.DocType;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.Namespace;
import org.xml.sax.SAXException;

public class TP {

    public static void transformaHtml(String file_xml, String file_xsl, String nomeF) {
        Document doc = XMLJDomFunctions.lerDocumentoXML(file_xml);
        if (doc != null) {
            Document novo = JDOMFunctions_XSLT.transformaDocumento(doc, file_xml, file_xsl);
            XMLJDomFunctions.escreverDocumentoParaFicheiro(novo, nomeF);
        }
    }

    public static String validarModelo() throws IOException {
        if (TP.validaDocumentoDTD("paises.xml", "paises.dtd") && TP.validaDocumentoXSD("paises.xml", "paises.xsd") && TP.validaDocumentoDTD("factos.xml", "factos.dtd")
                && TP.validaDocumentoXSD("factos.xml", "factos.xsd")) {
            return "DOCUMENTO VALIDO";
        } else {
            return "!!DOCUMENTO NAO VALIDO!!";
        }

    }

    public static Boolean validaDocumentoDTD(String xmlFile, String dtdFile) throws IOException {
        Document doc = XMLJDomFunctions.lerDocumentoXML(xmlFile);
        File f = new File(dtdFile);

        if (doc != null && f.exists()) { //DTD e XML existem

            Element raiz = doc.getRootElement();
            //Atribuir DTD ao	ficheiro XML
            DocType dtd = new DocType(raiz.getName(), dtdFile);
            doc.setDocType(dtd);

            //Gravar o XML com as alterações em disco
            XMLJDomFunctions.escreverDocumentoParaFicheiro(doc, xmlFile);

            //CHAMAR A FUNÇÃO DE VALIDAÇÃO por DTD
            Document docDTD = JDOMFunctions_Validar.validarDTD(xmlFile);

            if (docDTD == null) {
                System.out.println("INVALIDO");
                return false;
            } else {
                System.out.println("VALIDO");
                return true;
            }
        } else {
            return false;
        }
    }

    public static Boolean validaDocumentoXSD(String xmlFile, String xsdFile) {
        Document doc = XMLJDomFunctions.lerDocumentoXML(xmlFile);
        File f = new File(xsdFile);

        if (doc != null && f.exists()) {//XSD e XML existem
            Element raiz = doc.getRootElement();
            //Atribuir XSD ao ficheiro XML
            Namespace XSI = Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

            raiz.addNamespaceDeclaration(XSI);

            raiz.setAttribute(new Attribute("noNamespaceSchemaLocation", xsdFile, Namespace.getNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance")));

            //Gravar o XML com as alterações em	disco
            XMLJDomFunctions.escreverDocumentoParaFicheiro(doc, xmlFile);

            //CHAMAR A FUNÇÃO DE VALIDAÇÃO por XSD
            Document docXSD = JDOMFunctions_Validar.validarXSD(xmlFile);
            if (docXSD == null) {
                System.out.println("INVALIDO por XSD");
                return false;
            } else {
                System.out.println("VALIDO por XSD");
                return true;
            }
        } else {
            return false;
        }
    }

    public static ArrayList<String> verificaPais(String procura, String tipoPesquisa, String xmlFile) throws IOException {
        procura = convertToUpper(procura);
        ArrayList<String> ISO = new ArrayList<String>();
        Element raiz;
        Document doc = XMLJDomFunctions.lerDocumentoXML(xmlFile);
        if (doc == null) {
            return ISO;
        } else {
            raiz = doc.getRootElement();
        }

        List todosPaises = raiz.getChildren("Pais");
        for (int i = 0; i < todosPaises.size(); i++) {
            Element pais = (Element) todosPaises.get(i);
            if (tipoPesquisa.equals("Idiomas")) {
                List todosIdiomas = pais.getChildren(tipoPesquisa);
                for (int ii = 0; ii < todosIdiomas.size(); ii++) {
                    Element idioma = (Element) todosIdiomas.get(ii);
                    if (idioma.getChild("Idioma").getText().equals(procura)) {
                        ISO.add(pais.getAttributeValue("ISO"));
                    }
                }
            } else {
                if (pais.getChild(tipoPesquisa).getText().contains(procura)) {
                    ISO.add(pais.getAttributeValue("ISO"));
                }
            }
        }
        return ISO;
    }

    public static ArrayList<String> verificaISO2Argumentos(ArrayList<String> aux, String tipoPesquisa, String nomePesquisa, String xmlFile) throws IOException {
        nomePesquisa = convertToUpper(nomePesquisa);
        ArrayList<String> ISO = new ArrayList<String>();
        Element raiz;
        if (aux.isEmpty()) {
            return ISO;
        }
        Document doc = XMLJDomFunctions.lerDocumentoXML(xmlFile);
        if (doc == null) {
            return null;
        } else {
            raiz = doc.getRootElement();
        }

        List todosPaises = raiz.getChildren("Pais");

        for (int i = 0; i < todosPaises.size(); i++) {
            Element pais = (Element) todosPaises.get(i);
            for (int ii = 0; ii < aux.size(); ii++) {
                if (pais.getAttributeValue("ISO").equals(aux.get(ii)) && pais.getChild(tipoPesquisa).getText().equals(nomePesquisa)) {
                    ISO.add(pais.getAttributeValue("ISO"));
                }
            }
        }
        return ISO;
    }

    public static String verificaPaisMaiorArea() throws IOException {
        String aux = null;
        int area = 0;
        Element raiz;
        Document doc = XMLJDomFunctions.lerDocumentoXML("factos.xml");
        if (doc == null) {
            return null;
        } else {
            raiz = doc.getRootElement();
        }

        List todosPaises = raiz.getChildren("Pais");

        for (int i = 0; i < todosPaises.size(); i++) {
            Element pais = (Element) todosPaises.get(i);
            if (Integer.parseInt(pais.getChild("Area").getText()) > area) {
                area = Integer.parseInt(pais.getChild("Area").getText());
                aux = pais.getAttributeValue("ISO");
            }
        }
        return aux;
    }

    public static String removePais(String procura) { //REMOVE PAIS DE paises.XML E OS FACTOS EM factos.XML
        procura = convertToUpper(procura);  //Formatar a palavra introduzida pelo user

        Element raiz;
        Document doc = XMLJDomFunctions.lerDocumentoXML("paises.xml");

        if (doc == null) {
            return null;
        } else {
            raiz = doc.getRootElement();
        }
        List todosPaises = raiz.getChildren("Pais");

        for (int i = 0; i < todosPaises.size(); i++) {
            Element pais = (Element) todosPaises.get(i);
            if (pais.getChild("Nome").getText().equals(procura)) {
                String iso = pais.getAttributeValue("ISO");
                pais.getParent().removeContent(pais);
                XMLJDomFunctions.escreverDocumentoParaFicheiro(doc, "paises.xml");
                return iso;
            }
        }
        return null;
    }

    public static String removeFactos(String procura) {
        Element raiz;
        String res = null;
        Document doc = XMLJDomFunctions.lerDocumentoXML("factos.xml");

        if (doc == null) {
            return null;
        } else {
            raiz = doc.getRootElement();
        }

        List todosPaises = raiz.getChildren("Pais");

        for (int i = 0; i < todosPaises.size(); i++) {
            Element factos = (Element) todosPaises.get(i);
            if (factos.getAttributeValue("ISO").equals(procura)) {
                factos.getParent().removeContent(factos);
                XMLJDomFunctions.escreverDocumentoParaFicheiro(doc, "factos.xml");
                return res;
            }
        }
        return null;
    }

    public static String alterarPopulacao(String procura, String nova) throws IOException {
        procura = convertToUpper(procura);
        String res = null;
        res = returnISOFromName(procura);

        if (res == null) {
            return "Pais nao encontrado";
        }

        nova = convertToUpper(nova);
        Element raiz;
        Document doc = XMLJDomFunctions.lerDocumentoXML("factos.xml");

        if (doc == null) {
            return "O ficheiro factos.xml não existe";
        } else {
            raiz = doc.getRootElement();
        }

        List todosPaises = raiz.getChildren("Pais");

        for (int i = 0; i < todosPaises.size(); i++) {
            Element factos = (Element) todosPaises.get(i);
            if (factos.getAttributeValue("ISO").equals(res)) {
                factos.getChild("Populacao").setText(nova);
                XMLJDomFunctions.escreverDocumentoParaFicheiro(doc, "factos.xml");
                res = "Populacao do pais " + procura + " alterada com sucesso!" + validarModelo();
                return res;
            }
        }
        return "Nenhum pais chamado " + procura + " foi encontrado no ficheiro factos.xml";
    }

    public static String alterarIdioma(String procura, String procura2, String nova) throws IOException {
        procura = convertToUpper(procura);
        procura2 = convertToUpper(procura2);
        String res = new String();
        res = returnISOFromName(procura);

        if (res == null) {
            return "Pais nao encontrado";
        }

        nova = convertToUpper(nova);
        Element raiz;
        Document doc = XMLJDomFunctions.lerDocumentoXML("factos.xml");

        if (doc == null) {
            return "O ficheiro factos.xml não existe";
        } else {
            raiz = doc.getRootElement();
        }

        List todosPaises = raiz.getChildren("Pais");

        for (int i = 0; i < todosPaises.size(); i++) {
            Element factos = (Element) todosPaises.get(i);
            if (factos.getAttributeValue("ISO").equals(res)) {
                List aux = factos.getChildren("Idiomas");
                for (int aa = 0; aa < aux.size(); aa++) {
                    Element as = (Element) aux.get(aa);
                    List todosIdiomas = as.getChildren("Idioma");
                    for (int ii = 0; ii < todosIdiomas.size(); ii++) {
                        Element idioma = (Element) todosIdiomas.get(ii);
                        if (idioma.getText().equals(procura2)) {
                            idioma.setText(nova);
                            XMLJDomFunctions.escreverDocumentoParaFicheiro(doc, "factos.xml");
                            res = "Idioma do pais " + procura + " alterada com sucesso!" + validarModelo();
                            return res;
                        }
                    }
                }
                return "Idioma nao encontrado!!";
            }
        }
        return "Nenhum pais chamado " + procura + " foi encontrado no ficheiro factos.xml";
    }

    public static String alterarCapital(String procura, String nova) throws IOException {
        procura = convertToUpper(procura);
        String res = null;
        res = returnISOFromName(procura);

        if (res == null) {
            res = "Pais nao encontrado";
            return res;
        }

        nova = convertToUpper(nova);
        Element raiz;
        Document doc = XMLJDomFunctions.lerDocumentoXML("factos.xml");

        if (doc == null) {
            res = "O ficheiro factos.xml não existe";
            return res;
        } else {
            raiz = doc.getRootElement();
        }

        List todosPaises = raiz.getChildren("Pais");

        for (int i = 0; i < todosPaises.size(); i++) {
            Element factos = (Element) todosPaises.get(i);
            if (factos.getAttributeValue("ISO").equals(res)) {
                factos.getChild("Capital").setText(nova);
                XMLJDomFunctions.escreverDocumentoParaFicheiro(doc, "factos.xml");
                res = "Capital do pais " + procura + " alterada com sucesso!" + validarModelo();
                return res;
            }
        }
        res = "Nenhum pais chamado " + procura + " foi encontrado no ficheiro factos.xml";
        return res;
    }

    public static String returnISOFromName(String procura) {
        String res = null;
        Element raiz;
        Document doc = XMLJDomFunctions.lerDocumentoXML("paises.xml");

        if (doc == null) {
            return null;
        } else {
            raiz = doc.getRootElement();
        }

        List todosPaises = raiz.getChildren("Pais");

        for (int i = 0; i < todosPaises.size(); i++) {
            Element factos = (Element) todosPaises.get(i);
            if (factos.getChild("Nome").getText().equals(procura)) {
                return factos.getAttributeValue("ISO");
            }
        }
        return null;
    }

    public static String pesquisaemFicheiro(String nome_ficheiro, String pesquisa) {
        Document doc = XMLJDomFunctions.lerDocumentoXML(nome_ficheiro);
        String resultados = new String();
        List res_pesquisa = JaxenFunctions_XPATH.pesquisaXPath(doc, pesquisa); //vai receber o doc XML e a pesquisa XPATH
        if (res_pesquisa.isEmpty()) {
            return resultados;
        }
        resultados = JaxenFunctions_XPATH.listarResultado(res_pesquisa);

        return resultados; //retorna os resultados 
    }

    public static String pesquisaemPaises(String pesquisa) {
        String aux = pesquisaemFicheiro("paises.xml", pesquisa);

        if (aux.isEmpty()) {
            return null;
        }

        return aux;
    }

    public static String pesquisaemFactos(String pesquisa) {
        return pesquisaemFicheiro("factos.xml", pesquisa);
    }

    public static String mostraPais(String procura) {
        procura = convertToUpper(procura);
        String res = new String();
        res = "ISO: " + procura + "\n";
        res = res + "Nome: ";
        res = res + pesquisaemPaises("/Paises/Pais[@ISO=\"" + procura + "\"]/Nome") + "\n";
        res = res + "Continente: ";
        res = res + pesquisaemPaises("/Paises/Pais[@ISO=\"" + procura + "\"]/Continente") + "\n";
        res = res + "Nome do presidente: ";
        res = res + pesquisaemPaises("/Paises/Pais[@ISO=\"" + procura + "\"]/Nome_Presidente") + "\n";
        res = res + "Link da Bandeira: ";
        res = res + pesquisaemPaises("/Paises/Pais[@ISO=\"" + procura + "\"]/Link_da_Bandeira") + "\n";
        //res = res.trim();
        if (res == null) {
            return null;
        }
        res = res + "Codigo de Telefone: ";
        res = res + pesquisaemFactos("/Factos/Pais[@ISO=\"" + procura + "\"]/CodigoT") + "\n";
        res = res + "Codigo de Internet: ";
        res = res + pesquisaemFactos("/Factos/Pais[@ISO=\"" + procura + "\"]/CodigoI") + "\n";
        res = res + "Capital: ";
        res = res + pesquisaemFactos("/Factos/Pais[@ISO=\"" + procura + "\"]/Capital") + "\n";
        res = res + "Cidade com mais populacao: ";
        res = res + pesquisaemFactos("/Factos/Pais[@ISO=\"" + procura + "\"]/Cidade_Max_Populacao") + "\n";
        res = res + "Nome do Hino: ";
        res = res + pesquisaemFactos("/Factos/Pais[@ISO=\"" + procura + "\"]/Nome_Hino") + "\n";
        res = res + "Moeda: ";
        res = res + pesquisaemFactos("/Factos/Pais[@ISO=\"" + procura + "\"]/Moeda") + "\n";
        res = res + "Populacao: ";
        res = res + pesquisaemFactos("/Factos/Pais[@ISO=\"" + procura + "\"]/Populacao") + "\n";
        res = res + "Area: ";
        res = res + pesquisaemFactos("/Factos/Pais[@ISO=\"" + procura + "\"]/Area") + " km²\n";
        res = res + "Idiomas: ";
        res = res + pesquisaemFactos("/Factos/Pais[@ISO=\"" + procura + "\"]/Idiomas/Idioma/text()") + "\n";
        return res;
    }

    public static String mostraTodosPais() {
        File f = new File("paises.xml");
        if (f.exists()) {

            String res = pesquisaemPaises("/Paises/Pais/@ISO");
            String aux = new String();
            List<String> ISO = new ArrayList<String>(Arrays.asList(res.split("\n")));
            if (ISO.isEmpty()) {
                return "Nao existem paises no ficheiro!!";
            }
            for (int i = 0; i < ISO.size(); i++) {
                aux = aux + mostraPais(ISO.get(i)) + "\n";
            }

            return aux;
        } else {
            return "Ficheiros nao existem, logo nao existem paises guardados!!";
        }
    }

    public static void adicionaPais(Pais aux) {
        Element raiz;
        Document doc = XMLJDomFunctions.lerDocumentoXML("paises.xml");

        if (doc == null) {
            raiz = new Element("Paises"); //cria <catalogo>...</catalogo>
            doc = new Document(raiz);
        } else {
            raiz = doc.getRootElement();
        }

        Element pais = new Element("Pais");
        String iso = (aux.getISO());
        Attribute a = new Attribute("ISO", iso);
        pais.setAttribute(a);

        Element nome = new Element("Nome").addContent(aux.getNome());
        pais.addContent(nome);

        Element continente = new Element("Continente").addContent(aux.getContinente());
        pais.addContent(continente);

        Element presidente = new Element("Nome_Presidente").addContent(aux.getNome_presidente());
        pais.addContent(presidente);

        Element link = new Element("Link_da_Bandeira").addContent(aux.getLink());
        pais.addContent(link);

        raiz.addContent(pais);
        XMLJDomFunctions.escreverDocumentoParaFicheiro(doc, "paises.xml");
    }

    public static void adicionaFactos(Factos aux) {
        Element raiz;
        Document doc = XMLJDomFunctions.lerDocumentoXML("factos.xml");

        if (doc == null) {
            raiz = new Element("Factos"); //cria <catalogo>...</catalogo>
            doc = new Document(raiz);
        } else {
            raiz = doc.getRootElement();
        }

        Element factos = new Element("Pais");
        String iso = aux.getISO();
        Attribute a = new Attribute("ISO", iso);
        factos.setAttribute(a);

        Element codT = new Element("CodigoT").addContent(aux.getCod_telefone());
        factos.addContent(codT);

        Element codI = new Element("CodigoI").addContent(aux.getCod_internet());
        factos.addContent(codI);

        Element capital = new Element("Capital").addContent(aux.getCapital());
        factos.addContent(capital);

        Element cidadeM = new Element("Cidade_Max_Populacao").addContent(aux.getCidade_max());
        factos.addContent(cidadeM);

        Element hino = new Element("Nome_Hino").addContent(aux.getHino());
        factos.addContent(hino);

        Element moeda = new Element("Moeda").addContent(aux.getMoeda());
        factos.addContent(moeda);

        Element populacao = new Element("Populacao").addContent(aux.getPopulacao());
        factos.addContent(populacao);

        Element area = new Element("Area").addContent(aux.getArea());
        factos.addContent(area);

        Element idioma = new Element("Idiomas");
        for (int i = 0; i < aux.getIdiomas().size(); i++) {
            idioma.addContent(new Element("Idioma").addContent(aux.getIdiomas().get(i)));
        }
        factos.addContent(idioma);

        raiz.addContent(factos);
        XMLJDomFunctions.escreverDocumentoParaFicheiro(doc, "factos.xml");
    }

    public static String convertToUpper(String frase) {
        String upper_case_line = "";
        Scanner lineScan = new Scanner(frase);
        while (lineScan.hasNext()) {
            String word = lineScan.next();
            if (word.equals("do") || word.equals("de") || word.equals("da")) {
                upper_case_line += word + " ";
            } else {
                upper_case_line += Character.toUpperCase(word.charAt(0)) + word.substring(1) + " ";
            }
        }
        upper_case_line = upper_case_line.substring(0, upper_case_line.length() - 1);
        return upper_case_line;
    }

    public static String procuraNome(String procura) throws FileNotFoundException {
        String er1 = "^<title>([a-zA-Z\\s\\su00C0-\\u00FF]*)\\s*–\\s*Wik";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        String palavra = null;
        Scanner ler = new Scanner(new FileInputStream("wiki.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                System.out.println("Nome Pais: " + m1.group(1));
                palavra = m1.group(1);
            }
        }
        ler.close();
        return palavra;
    }

    public static String procuraBandeira(String procura) throws FileNotFoundException {
        String er1 = "title=\"Bandeira[a-zA-Z\\s\\su00C0-\\u00FF=<>\"]*src=\"([a-zA-Z0-9_-[.]/%]*)\"";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        String palavra = null;
        Scanner ler = new Scanner(new FileInputStream("wiki.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                System.out.println("Link bandeira: " + m1.group(1));
                palavra = m1.group(1);
                break;
            }
        }
        ler.close();
        return "http:" + palavra;
    }

    public static String procuraContinente(String procura) throws FileNotFoundException {
        String er1 = procura + "</a></td><td>[a-zA-Z\\s\\su00C0-\\u00FF]*</td><td>([a-zA-Z\\s\\su00C0-\\u00FF]*)</td>";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        String palavra = null;
        Scanner ler = new Scanner(new FileInputStream("sport.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                System.out.println("Continente: " + m1.group(1));
                palavra = m1.group(1);
            }
        }
        ler.close();
        return palavra;
    }

    public static String procuraHino(String procura) throws FileNotFoundException {
        String er1 = "title=\"Hino nacional\">Hino nacional</a>: [<i>]*<a href=\"/wiki/[a-zA-Z0-9\\s\"_%-=!]*\" title=\"([a-zA-Z\\s\\su00C0-\\u00FF!]*)\">";
        String er2 = "title=\"Hino nacional\">Hino nacional</a>: [<i>]*<a href=\"/wiki/[a-zA-Z0-9\\s\"_%-=!]*\" title=\"[a-zA-Z\\s\\su00C0-\\u00FF]*\">\"([a-zA-Z\\s\\su00C0-\\u00FF']*)\"";
        String er3 = "title=\"Hino nacional\">Hino nacional</a>:[a-zA-Z\\s\\su00C0-\\u00FF=<>/\"]*<i>([a-zA-Z\\s\\su00C0-\\u00FF]*)</i>";
        Pattern p1 = Pattern.compile(er1);
        Pattern p2 = Pattern.compile(er2);
        Pattern p3 = Pattern.compile(er3);
        String linha;
        String palavra = null;
        Scanner ler = new Scanner(new FileInputStream("wiki.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            Matcher m2 = p2.matcher(linha);
            Matcher m3 = p3.matcher(linha);
            if (m1.find()) {
                System.out.println("Hino: " + m1.group(1));
                palavra = m1.group(1);
            }
            if (m2.find()) {
                System.out.println("Hino: " + m2.group(1));
                palavra = m2.group(1);
            }
            if (m3.find()) {
                System.out.println("Hino: " + m3.group(1));
                palavra = m3.group(1);
            }
        }
        ler.close();
        return palavra;
    }

    public static String procuraCapital(String procura) throws FileNotFoundException {
        String er1 = "<td style=\"width:50%;border-top:solid 1px #ccd2d9; padding:0.4em 1em 0.4em 0; vertical-align:top\"><a href=\"/wiki/[a-zA-Z\\s=\"-_0-9%\\s]*\" title=\"([a-zA-Z\\s\\su00C0-\\u00FF]*)[(\"]?";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("wiki.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                System.out.println("Capital: " + m1.group(1));
                return m1.group(1);
            }

        }
        ler.close();
        return null;
    }

    public static String procuraCidadeMax(String procura) throws FileNotFoundException {
        String er1 = "<td style=\"border-top:solid 1px #ccd2d9; padding:0.4em 1em 0.4em 0; vertical-align:top\"><a href=\"/wiki/[a-zA-Z0-9%_]*\" title=\"([a-zA-Z\\s\\su00C0-\\u00FF]*)\">[a-zA-Z\\su00C0-\\u00FF]*</a>";
        String er2 = "<td style=\"border-top:solid 1px #ccd2d9; padding:0.4em 1em 0.4em 0; vertical-align:top\">([a-zA-Z\\s\\su00C0-\\u00FF]*)$";
        String er3 = "^<td style=\"border-top:solid 1px #ccd2d9; padding:0.4em 1em 0.4em 0; vertical-align:top\">([a-zA-Z\\s]*)<sup id=\"cite_ref-population_1-0\" class=\"reference\">";
        Pattern p1 = Pattern.compile(er1);
        Pattern p2 = Pattern.compile(er2);
        Pattern p3 = Pattern.compile(er3);
        String linha;
        String palavra = null;
        Scanner ler = new Scanner(new FileInputStream("wiki.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            Matcher m2 = p2.matcher(linha);
            Matcher m3 = p3.matcher(linha);
            if (m1.find()) {
                System.out.println("Cidade max: " + m1.group(1));
                palavra = m1.group(1);
                break;
            }
            if (m2.find()) {
                System.out.println("Cidade max: " + m2.group(1));
                palavra = m2.group(1);
                break;
            }
            if (m3.find()) {
                System.out.println("Cidade max: " + m3.group(1));
                palavra = m3.group(1);
                break;
            }
        }
        ler.close();
        return palavra;
    }

    public static String procuraPresidente(String procura) throws FileNotFoundException {
        String er1 = "<td style=\"padding:0 1em 0.2em 0; text-align:left; vertical-align:top\">&#160;- <a href=\"/wiki/.+>(Presidente)</a>";
        String er2 = ">([a-zA-Z-ğüşöçİĞÜŞÖÇ\\s\\su00C0-\\u00FF\\s]*)</a>";
        Pattern p1 = Pattern.compile(er1);
        Pattern p2 = Pattern.compile(er2);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("wiki.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                linha = ler.nextLine();
                linha = ler.nextLine();
                Matcher m2 = p2.matcher(linha);
                if (m2.find()) {
                    System.out.println("Presidente: " + m2.group(1));
                    return m2.group(1);
                }
            }
        }
        return "Nao e uma republica, logo nao tem presidente da Republica";
    }

    public static String procuraArea(String procura) throws FileNotFoundException {
        String er1 = "Área</strong> : ([0-9\\s]*) km²</p>";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("sport_country.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                String a = m1.group(1);
                a = a.replaceAll("\\s", "");
                System.out.println("area: " + a);
                return a;
            }
        }
        ler.close();
        return null;
    }

    public static String procuraISO(String procura) throws FileNotFoundException {
        String er1 = procura + "</a>([a-zA-Z\\s\\su00C0-\\u00FF0-9<>/=\"_-[#]\\[\\]]*</span></a></sup></td>)*";
        String er2 = "^<td>([A-Z]{3})(</td>)?";
        Pattern p1 = Pattern.compile(er1);
        Pattern p2 = Pattern.compile(er2);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("wiki_ISO.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                linha = ler.nextLine();
                linha = ler.nextLine();
                linha = ler.nextLine();
                Matcher m2 = p2.matcher(linha);
                if (m2.find()) {
                    System.out.println("ISO: " + m2.group(1));
                    return m2.group(1);
                }
            }
        }
        ler.close();
        return null;
    }

    public static String procuraLinkPais(String procura) throws FileNotFoundException {
        String er1 = "<a href=\"([a-zA-Z/._]*)\">" + procura + "</a>";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("sport.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                return m1.group(1);
            }
        }
        return null;
    }

    public static String getPopulation() throws FileNotFoundException {
        String er1 = "População</strong> : ([0-9\\s]*)</p>";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("sport_country.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                String a = m1.group(1);
                a = a.replaceAll("\\s", "");
                System.out.println("Populacao: " + a);
                return a;
            }
        }
        return null;
    }

    public static String getCoin() throws FileNotFoundException {
        String er1 = "Moeda</strong> : ([a-zA-Z\\su00C0-\\u00FF\\s-_]*)\\s*\\(";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("sport_country.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                System.out.println("Moeda: " + m1.group(1));
                return m1.group(1);
            }
        }
        return null;
    }

    public static String getTelephoneCode() throws FileNotFoundException {
        String er1 = "^<td style=\"border-top:solid 1px #ccd2d9; padding:0.4em 1em 0.4em 0; vertical-align:top\"><code>(\\+[0-9]*)</code>?";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("wiki.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                System.out.println("Codigo telefone: " + m1.group(1));
                return m1.group(1);
            }
        }
        return null;
    }

    public static String getInternetCode() throws FileNotFoundException {
        String er1 = "^<td style=\"border-top:solid 1px #ccd2d9; padding:0.4em 1em 0.4em 0; vertical-align:top\"><a href=\"/wiki/\\.[a-z]*\" title=\".[a-z]*\">(.[a-z]*)</a>";
        Pattern p1 = Pattern.compile(er1);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("wiki.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                System.out.println("Codigo internet: " + m1.group(1));
                return m1.group(1);
            }
        }
        return null;
    }

    public static ArrayList<String> getIdiomas() throws FileNotFoundException {
        ArrayList<String> idiomas = new ArrayList<String>();
        String er1 = "Língua oficial</a></b>";
        Pattern p1 = Pattern.compile(er1);
        String er2 = "title=\"[a-zA-Z\\su00C0-\\u00FF\\s]*\">([a-zA-Z\\su00C0-\\u00FF\\s]*)</a>";
        Pattern p2 = Pattern.compile(er2);
        String er3 = "title=\"[a-zA-Z\\su00C0-\\u00FF\\s]*\">([a-zA-Z\\su00C0-\\u00FF\\s]*)</a>";
        Pattern p3 = Pattern.compile(er3);
        String linha;
        Scanner ler = new Scanner(new FileInputStream("wiki.html"));
        while (ler.hasNextLine()) {
            linha = ler.nextLine();
            Matcher m1 = p1.matcher(linha);
            if (m1.find()) {
                linha = ler.nextLine();
                linha = ler.nextLine();

                Matcher m2 = p2.matcher(linha);

                while (m2.find()) {
                    idiomas.add(convertToUpper(m2.group(1)));
                }
                linha = ler.nextLine();
                Matcher m3 = p3.matcher(linha);
                while (m3.find()) {
                    idiomas.add(convertToUpper(m3.group(1)));
                }
            }
        }
        ler.close();
        return idiomas;
    }

    public static String procuraInfoPais(String pesquisa) throws IOException {
        String nome, bandeira, hino, continente, capital, cidademax, presidente, iso, codt, codi, coin, area, pop;
        pesquisa = convertToUpper(pesquisa);
        ArrayList<String> idiomas = new ArrayList<String>();
        String link1 = "https://pt.wikipedia.org/wiki/";
        String link2 = "https://www.sport-histoire.fr/pt/Geografia/Paises_por_ordem_alfabetica.php";
        String link3 = "https://pt.wikipedia.org/wiki/Compara%C3%A7%C3%A3o_entre_c%C3%B3digos_de_pa%C3%ADses_COI,_FIFA,_e_ISO_3166";
        String link4 = "https://www.sport-histoire.fr/pt/Geografia/";
        HttpRequestFunctions.httpRequest1(link1, pesquisa, "wiki.html");
        HttpRequestFunctions.httpRequest1(link2, "", "sport.html");
        HttpRequestFunctions.httpRequest1(link3, "", "wiki_ISO.html");

        nome = procuraNome(pesquisa).trim();
        bandeira = procuraBandeira(nome);
        hino = procuraHino(nome);
        continente = procuraContinente(nome);
        capital = procuraCapital(nome);
        cidademax = procuraCidadeMax(nome);
        presidente = procuraPresidente(nome);
        iso = procuraISO(nome);
        codt = getTelephoneCode();
        codi = getInternetCode();
        link4 = link4 + procuraLinkPais(nome);
        HttpRequestFunctions.httpRequest1(link4, "", "sport_country.html");
        area = procuraArea(nome);
        pop = getPopulation();
        coin = getCoin();
        idiomas = getIdiomas();

        Pais aux = new Pais(iso, nome, continente, presidente, bandeira);
        Factos aux2 = new Factos(iso, codt, codi, capital, cidademax, hino, coin, pop, area, idiomas);
        adicionaPais(aux);
        adicionaFactos(aux2);
        return iso;
    }

    public static void main(String[] args) throws IOException {
        NewJFrame f = new NewJFrame();
        f.setVisible(true);
    }
}
