import java.io.*;
import java.util.*;

public class TSP{

	int ciudadesTotal;
	Vector<Ciudad> ciudades;
	Vector<Integer> temporal; 
    Vector<Integer> total;
    float resultado = 0;
    float[][] tablero;   //Array para guardar las distancias entre ciudades
    float[][] permutaciones;    //Array para encontrar costo más bajo
    float[][] camino;

    public static void main(String[] args){
		
		TSP uno = new TSP();
		String Archivo;
		Scanner teclado = new Scanner(System.in);
		System.out.println("Ingrese el nombre del archivo de texto");
		Archivo = teclado.nextLine();
		teclado.close();
        uno.LeerArch(Archivo);
		uno.LlamarFB(0);
		System.out.println("---------------------------------");
		uno.LlamarPD(0);
 
    }
    
    public void LlamarFB(int a){//función que llama a la Fuerza Bruta y calcula el tiempo de ejecución 
		preparFB();	
		long i = System.nanoTime();
		System.out.println("Usando Fueza bruta");
		FuerzaB(a);
		Res();
		long j = System.nanoTime();
		System.out.println("Tiempo: " + (j - i)/(Math.pow(10,9)) + " segundos" );
	}
    public void LlamarPD(int a){ //función que llama a Programación dinámica y calcula el tiempo de ejecución
		preparPD();
		long i = System.nanoTime();
		System.out.println("Usando Programación Dinamica");
		ProD(a);
		Res();
		long j = System.nanoTime();
		System.out.println("Tiempo: " + (j - i)/(Math.pow(10,9)) + " segundos" );
	}
    
    public void LeerArch(String archivo){//lee el archivo 
		
		try{
			Scanner S = new Scanner(new File(archivo));			
			ciudadesTotal = S.nextInt();
			System.out.println("Total de ciudades: " + ciudadesTotal);
			ciudades = new Vector<Ciudad>(ciudadesTotal);			
			for(int i = 0; i < ciudadesTotal; i++){
				float x = S.nextFloat();
				float y = S.nextFloat();
				Ciudad c = new Ciudad (x,y);
				ciudades.add(c);
			}	
			S.close();
			
		}catch(FileNotFoundException e){
			
			System.out.println(e);	
		}
	}	
	public void imprimir(){//imprime las coordenadas de cada ciudad 
		for (int i = 0; i < ciudadesTotal; i++){
			System.out.println("Ciudad " + i + " esta en las coordenadas x: " + ciudades.get(i).getX() + " y: " +ciudades.get(i).getY());
		}
	}
	public void FuerzaB(int inicio){//funcion de fuerza bruta
		
		for(int i = inicio; i < ciudadesTotal; i++){   //Se encuentran todas las permutaciones posibles con recursividad
            intercambiar(i,inicio);
            FuerzaB(inicio +1);
            intercambiar(inicio,i);
        }
        if (inicio == ciudadesTotal-1){    //Si la permutacion esta completa
			float T = calcularTotal();
            if(T <= resultado)
            {
                resultado = T; //Se iguala el costo nuevo de la nueva permutacion
                total = temporal; //Se iguala el resultado a la permutacion correspondiente
            }				
		}       
	}	
	public void preparFB(){
		
		temporal = new Vector<Integer>(ciudadesTotal);
		total = new Vector<Integer>(ciudadesTotal);
		for(int i = 0; i < ciudadesTotal; i++)
		{
			temporal.add(i);
			total.add(i);
		}		
		resultado = calcularTotal();
	}
	public void intercambiar(int a, int b)  //funcion para intercambiar dos elementos de lugar en las permutaciones
    {
        int temp;
        int temp2 = temporal.get(b);
        temp = temporal.get(a);
        temporal.setElementAt(temp2, a);
        temporal.setElementAt(temp, b);
    }
    public float calcularTotal(){
		float c = 0;
        for(int i = 1; i < ciudadesTotal; i++)
        {
            c += distancia(temporal.get(i-1),temporal.get(i)); 
        }
        c += distancia(temporal.get(0), temporal.get(ciudadesTotal-1)); //Costo del ultimo elemento al elemento inicial
        return c;
	}
	public float distancia(int a, int b) //devuelve la distancia entre los dos puntos
    {
        return (float)(Math.sqrt(Math.pow(ciudades.get(a).getX() - ciudades.get(b).getX(), 2) + Math.pow(ciudades.get(a).getY() - ciudades.get(b).getY(), 2)));
    }
	public void Res(){//imprime la respuesta
		System.out.println("Orden visitado");
		for(int i = 0; i < ciudadesTotal; i++)
        {
            System.out.print(total.get(i));
            System.out.print(" -> ");
        }
        System.out.print(total.get(0));
        System.out.println("\nCosto Total: " + resultado);
	}
	public void preparPD(){//se llenan los datos para usar programación dinamica 
		tablero = new float[ciudadesTotal][ciudadesTotal];
		for(int i = 0; i < ciudadesTotal; i++)
		{
			for(int j = 0; j < ciudadesTotal; j++)
			{
				if(tablero[i][j] == 0.0)
				{
					float costo = distancia(i,j);
					tablero[i][j] = costo;
					tablero[j][i] = costo;
				}
			}
		}
		total = new Vector<Integer>(ciudadesTotal);
		permutaciones = new float[ciudadesTotal][(int) Math.pow(2, ciudadesTotal)];
		camino = new float[ciudadesTotal][(int) Math.pow(2, ciudadesTotal)];
		
		for(int i = 0; i < ciudadesTotal; i++)   //Se llenan los arreglos con -1 para saber qué ciudades no han sido visitadas
		{
			for(int j = 0; j < (int) Math.pow(2, ciudadesTotal); j++)
			{
				permutaciones[i][j]=-1;  
				camino[i][j]=-1;
			}
			 permutaciones[i][0] = tablero[i][0];
		}
	}
	
	public void ProD(int a){//funcion de programación dinamica 
		total.add(a);
		resultado = determinaCosto(a, (int)Math.pow(2, ciudadesTotal)-2);
		determinaCamino(a, (int)Math.pow(2, ciudadesTotal)-2);
		
	}
	
	public float determinaCosto(int inicio, int mascara){ //determina el costo total usando PD 
		
        int nuevaMascara, mascaraTemporal;  
        float res = -1;  
        
        if(permutaciones[inicio][mascara] == -1) {
            for(int i = 0; i < ciudadesTotal; i++){
                mascaraTemporal = (int)Math.pow(2, ciudadesTotal)-(int)Math.pow(2,i)-1;  
                nuevaMascara = mascara & mascaraTemporal;   
                if(nuevaMascara != mascara) 
                {
                    if(res == -1 || res > (tablero[inicio][i] + determinaCosto(i, nuevaMascara))) 
                    {
                        res = tablero[inicio][i] + (float)determinaCosto(i, nuevaMascara);  
                        camino[inicio][mascara] = i;  
                    }
                }
            }
            permutaciones[inicio][mascara] = res;   
            return res;
        }
        else
        {
            return permutaciones[inicio][mascara];
        }
    }
    public void determinaCamino(int inicio, int mascara) //Determina el camino a seguir usando PD
    {
        if(camino[inicio][mascara] == -1)  
        {
            return;
        }
        int nuevaMascara2 = mascara & (int)Math.pow(2, ciudadesTotal)-((int)Math.pow(2, (int)camino[inicio][mascara]))-1;    
        total.add((int)camino[inicio][mascara]); 
        determinaCamino((int)camino[inicio][mascara], nuevaMascara2); 
    }
}

class Ciudad {
    float x;
    float y;
    
    Ciudad(float a, float b)
    {
        x = a;
        y = b;
    }
    
    public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
}
