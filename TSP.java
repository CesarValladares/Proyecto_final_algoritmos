/*
 *  Proyecto 4: Problema del Agente Viajero(TSP)
 *  Fernando Alcantara Santana A01019595
 *  Prof: Dr. Víctor de la Cueva
 *  Análisis y Diseño de Algoritmos
 */

import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Vector;

public class TSP {
    
    Vector<Ciudad> ciudades;    //Vector para guardar los objetos de tipo Ciudad
    Vector<Integer> recorrido;  //Vector para guardar las permutaciones
    Vector<Integer> resultado;  //Vector para guardar la mejor permutación (mejor camino)
    float[][] distancias;   //Array para guardar las distancias entre ciudades
    float[][] posibilidades;    //Array para encontrar costo más bajo
    float[][] ruta; //Array para encontrar mejor camino
    int no_ciudades;
    float costoMenor = 0;
    
    public static void main(String[] args)
    {
        TSP problema = new TSP();
        
        problema.lecturaDatos("algo.txt", 1);    //Se inicializan las variables globales
        long inicio = System.nanoTime();    //medidor de tiempo (inicio)
        problema.FB(0); //Se llama la función para la fuerza bruta y que empiece desde el elemento 1 del vector
        long termino = System.nanoTime();   //medidor de tiempo (fin)
        problema.imprimirResultado(inicio, termino);
        
        /*problema.lecturaDatos("P4tsp10.txt", 2);    //Se inicializan las variables globales
        inicio = System.nanoTime(); //medidor de tiempo (inicio)
        problema.PD(); //Se llama la función que realiza el problema con programación dinámica
        termino = System.nanoTime();    //medidor de tiempo (fin)
        problema.imprimirResultado(inicio, termino);
        
        problema.lecturaDatos("P4tsp25.txt", 2);    //Se inicializan las variables globales
        inicio = System.nanoTime(); //medidor de tiempo (inicio)
        problema.PD();  //Se llama la función que realiza el problema con programación dinámica
        termino = System.nanoTime();    //medidor de tiempo (fin)
        problema.imprimirResultado(inicio, termino);*/
    }
    
    public float dist2p(int a, int b) //devuelve la distancia entre los dos puntos
    {
        return (float)(Math.sqrt(Math.pow(ciudades.get(a).x - ciudades.get(b).x, 2) + Math.pow(ciudades.get(a).y - ciudades.get(b).y, 2)));
    }
    
    public float costoTotal() //Devuelve el costo total del recorrido
    {
        float costo = 0;
        for(int h = 1; h < recorrido.size(); h++)
        {
            costo += dist2p(recorrido.get(h-1),recorrido.get(h)); 
        }
        costo += dist2p(recorrido.get(0), recorrido.get(recorrido.size()-1)); //Costo del último elemento al elemento inicial
        return costo;
    }
    
    public void FB(int j)   //Método por fuerza bruta
    {
        for(int i = j; i < recorrido.size(); i++)   //Se encuentran todas las permutaciones posibles con recursividad
        {
            swap(i,j);
            FB(j+1);
            swap(j,i);
        }
        if (j == recorrido.size()-1)    //Si la permutación está completa
        {
            float costo = costoTotal();
            if(costo <= costoMenor)
            {
                costoMenor = costo; //Se iguala el costo nuevo de la nueva permutación
                resultado = recorrido; //Se iguala el resultado a la permutación correspondiente
            }
        }
    }
    
    public void swap(int a, int b)  //función para intercambiar dos elementos de lugar en las permutaciones
    {
        int temp;
        temp = recorrido.get(a);
        recorrido.setElementAt(recorrido.get(b), a);
        recorrido.setElementAt(temp, b);
    }
    
    public void imprimirResultado(long a, long b)   //Función para imprimir los resultados y tiempos de ejecución finales
    {
        for(int i = 0; i < resultado.size(); i++)
        {
            System.out.print(resultado.get(i));
            System.out.print(" a ");
        }
        System.out.print(resultado.get(0));
        System.out.println("");
        System.out.println("Costo Total: " + costoMenor);
        System.out.println("El problema tardó " + (b-a) + " x10^-9 segundos");
        System.out.println("");
    }
    
    public void lecturaDatos(String archivo, int a)   //Para leer el archivo de texto y llenar el vector de la clase TSP
    {        
        Scanner escanner = null;    //Se crea un scanner para leer el archivo
        try {
           //archivo = TSP.class.getClassLoader().getResource(archivo).toString().replace("file:/",""); //Se asigna a la variable archivo el nombre completo de la dirección del archivo en el sistema
           escanner = new Scanner(new File(archivo));   //Se asigna el scanner creado con el archivo de texto cuidando que este exista
        } catch (FileNotFoundException | NullPointerException error) {     //En caso de que no exista el archivo
           System.out.println("No se encontró el archivo"); //Mensaje de error
           System.exit(0);  //El programa termina
        }
        
        no_ciudades = escanner.nextInt();  //número de ciudades según el documento
        
        ciudades = new Vector<Ciudad>(no_ciudades); //Se inicia el vector de ciudades de acuerdo al número que el archivo indica
        for(int j = 0; j < no_ciudades; j++)   //Llenar el vector de ciudades con sus coordenadas
        {
            ciudades.add(new Ciudad(escanner.nextFloat(),escanner.nextFloat()));
        }
        
        if(a == 1) //Con Fuerza Bruta
        {
            recorrido = new Vector<Integer>(no_ciudades);
            resultado = new Vector<Integer>(no_ciudades);
            for(int k = 0; k < no_ciudades; k++)
            {
                resultado.add(k);
                recorrido.add(k);
            }
            costoMenor = costoTotal(); //se inicia el valor del costo menor usando la primer permutación posible
        }
        else if(a == 2) //Con programación dinámica
        {
            distancias = new float[no_ciudades][no_ciudades];
            for(int l = 0; l < no_ciudades; l++)
            {
                for(int m = 0; m < no_ciudades; m++)
                {
                    if(distancias[l][m] == 0.0)
                    {
                        float costo = dist2p(l,m);
                        distancias[l][m] = costo;
                        distancias[m][l] = costo;
                    }
                }
            }
            resultado = new Vector<Integer>(no_ciudades);
            posibilidades = new float[no_ciudades][(int) Math.pow(2, no_ciudades)];
            ruta = new float[no_ciudades][(int) Math.pow(2, no_ciudades)];
            
            for(int g = 0; g < no_ciudades; g++)   //Se llenan los arreglos con -1 para saber qué ciudades no han sido visitadas
            {
                for(int h = 0; h < (int) Math.pow(2, no_ciudades); h++)
                {
                    posibilidades[g][h]=-1;  
                    ruta[g][h]=-1;
                }
                 posibilidades[g][0] = distancias[g][0];
            }
        }
    }
    
    public void PD() //Función que llena el Vector de resultado con el camino correspondiente y da el costo del mismo usando programación dinámica
    {
        resultado.add(0);   //Se ingresa la ciudad donde el agente comenzará su camino al vector que guardará el camino más corto
        costoMenor = determinaCosto(0, (int)Math.pow(2, no_ciudades)-2);   //Se iguala el costo más chico a la variable costoMenor
        determinaCamino(0, (int)Math.pow(2, no_ciudades)-2);    //Se obtiene el camino que seguirá el agente hasta volver a la ciudad incial
    }
    
    public float determinaCosto(int inicio, int mascara)
    {
        int nuevaMascara, mascaraTemporal;  //Se crean variables que guarden las máscaras para encontrar las ciudades a visitar y obtener su costo posteriormente
        float res = -1;  //Se inicia la variable que guardará el costo más bajo a -1
        
        if(posibilidades[inicio][mascara] == -1) //Si ya fue visitada
        {
            for(int i = 0; i < no_ciudades; i++)
            {
                mascaraTemporal = (int)Math.pow(2, no_ciudades)-(int)Math.pow(2,i)-1;   //Se crea una máscara temporal que cumpla con los requerimientos para ciudades no visitadas
                nuevaMascara = mascara & mascaraTemporal;   //Se realiza la operación binaria AND entre la máscara temporal y la actual para asignar una nueva ciudad como visitada y obtener una nueva máscara para ser usada recursivamente
                if(nuevaMascara != mascara) //Si aun hay ciudades por visitar
                {
                    if(res == -1 || res > (distancias[inicio][i] + determinaCosto(i, nuevaMascara)))    //Si el costo de la función está en la primer iteración o el costo nuevo es menor al resultado de la función
                    {
                        res = distancias[inicio][i] + (float)determinaCosto(i, nuevaMascara);  //Se asigna un nuevo resultado a la función
                        ruta[inicio][mascara] = i;  //Se guarda la ciudad en el arreglo de la ruta
                    }
                }
            }
            posibilidades[inicio][mascara] = res;   //Se guarda el resultado en el arreglo de posibilidades
            return res;
        }
        else
        {
            return posibilidades[inicio][mascara];
        }
    }
     
     public void determinaCamino(int inicio, int mascara) //Función que determina el camino que seguirá el agente viajero
    {
        if(ruta[inicio][mascara] == -1)  //Fin de la recursión
        {
            return;
        }
        int nuevaMascara = mascara & (int)Math.pow(2, no_ciudades)-((int)Math.pow(2, (int)ruta[inicio][mascara]))-1;    //Se realiza la operación binaria AND para asignar una nueva ciudad como visitada y obtener una nueva máscara
        resultado.add((int)ruta[inicio][mascara]);  //Se agrega la ciudad visitada al vector que contiene el camino del viajero
        determinaCamino((int)ruta[inicio][mascara], nuevaMascara);  //se llama de nuevo a la función con la nueva máscara para determinar las ciudades restantes por visitar
    }
}
