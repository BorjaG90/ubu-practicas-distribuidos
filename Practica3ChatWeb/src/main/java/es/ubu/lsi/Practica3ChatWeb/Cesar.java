package es.ubu.lsi.Practica3ChatWeb;

/**
 * Created by erwol on 04/03/2016 at 13:55
 * For further information visit http://erwol.com
 */
import java.util.Scanner;
import java.io.Console;
public class Cesar {
    private static Scanner lee = new Scanner(System.in);
 
    public static void main(String args[]){
        int op = -1;
        String texto = null;
        Console consola = System.console();
        int clave = 0;
        while(op != 0){
            System.out.print("\n\tCIFRADO CÉSAR\n0.- Salir\n1.- Cifrar\n2.- Descifrar\n> ");
            op = lee.nextInt();
            switch(op){
                case 0:break;
                case 1:
                    System.out.printf("1. Introduzca el texto a cifrar\n> ");
                    texto = consola.readLine();
                    System.out.printf("2. Introduzca la clave con la que se codificará el texto\n>");
                    clave = lee.nextInt();
                    System.out.println("3. Cadena cifrada con clave = " + clave + " ->" + cifrar(texto, clave) + "<-");
                    break;
                case 2:
                    System.out.printf("1. Introduzca el texto cifrado\n> ");
                    texto = consola.readLine();
                    System.out.printf("2. Introduzca la clave con la que codificó el texto\n> ");
                    clave = lee.nextInt();
                    System.out.println("3. Cadena cifrada con clave = " + clave + " ->" + descifrar(texto, clave) + "<-");
                    break;
                default:System.out.println("Ha introducido una opción no válida. Pruebe de nuevo.");
            }
        }
    }
 
    static String cifrar(String original, int clave){
        StringBuilder cifrado = new StringBuilder(original.length());
        for(int i = 0;i < original.length(); i++)
            cifrado.append((char)((int)(original.charAt(i)) + clave % 255));
        return cifrado.toString();
    }
 
    static String descifrar(String original, int clave){
        StringBuilder descifrado = new StringBuilder(original.length());
        for(int i = 0;i < original.length(); i++)
            descifrado.append((char)((int)(original.charAt(i)) - clave % 255));
        return descifrado.toString();
    }
}
