# AlgoritmoMultiobjetivoPmedianPdispersion

**DESCRIPCIÓN DEL ALGORITMO**

Algoritmo multiobjetivo pmedian pdispersion que evalúa la distancia entre las propias instalaciones para maximizarla (pdispersion)
y la distancia entre los otros puntos de interés y las instalaciones para minimizarla (pmedian).

El resultado de aplicar el algoritmo es una salida con N posibles combinaciones de diferentes lugares donde ubicar las instalaciones, todas igual de válidas.
Se genera una gráfica para visualizar el Frente de Pareto que forman ese conjunto de soluciones, 
antes y después de aplicar la búsqueda local para ver la mejora producida en la combinación de instalaciones.

**¿CÓMO SE EJECUTA?**

Para ejecutar el programa es necesario seguir los siguiente pasos:
  1. En el buscador se escribe cmd y abrimos la aplicación símbolo del sistema.
  2. En la consola nos posicionamos en la carpeta TFG_jar del proyecto denominada TFG. Para ello:
     1. Con el comando cd escribimos la ruta de ubicación de la carpeta del proyecto. Por ejemplo: cd C:\Users\pablo\OneDrive\Escritorio/TFG
     2. Una vez posicionados en la carpeta del proyecto, aacedemos a la carpeta TFG_jar del midmo de la siguiente manera: cd */out/artifacts/TFG_jar
  3. Una vez posicionados en el ejecutable del proyecto, simplemente nos queda lanzar la ejecución. Para ello escribimos: java -jar TFG.jar
  4. Presionams enter y la ejecución del proyecto se lanza.

  5. Por pantalla podemos ir viendo cómo se ejecuta cada grafo, mostrándose las soluciones y el tiempo de ejecución tanto antes como después de aplicar la búsqueda local.
          
![Captura](https://user-images.githubusercontent.com/63146846/155406254-eab86821-8981-4a4a-a771-7a27548fbd93.PNG)

  7. Además, si accedemos a la carpeta Graficas del proyecto, podemos ver la representación de cada grafo.
  8. Del mismo modo, accediendo a la carpeta Frentes, podemoas ver el desglose de los datos que constituyen cada gráfica.

![tempsnip](https://user-images.githubusercontent.com/63146846/155407184-f975700d-14f2-4fcc-8f60-dd43c6342707.png)


**¿CÓMO OBTENER LAS GRÁFICAS CON LOS FRENTES DE PARETO?**

Tras la ejecución de cada grafo, el algoritmo genera una imagen con la gráfica. El nombre de la imagen está formado por el nombre del grafo sucedido de la extensión .png
Un ejemplo de salida sería el siguiente, el cual corresponde al grafo pmed3 y, por tanto, la imagen tiene el nombre de pmed3.png

![pmed3 txt](https://user-images.githubusercontent.com/63146846/149678054-0548b66e-6757-4470-a03d-ed5557276161.png)

Las gráficas se guardan automaticamente en la misma carpeta del proyecto.

**FUNCIONAMIENTO Y EJECUCIÓN DEL MOEAFramework**

**RESULTADOS OBTENIDOS**

Para poder ver los resultados obtenidos, es necesario acceder al siguiente enlace, el cual lleva al PDF final con las soluciones: poner enlace

**CRÉDITOS, AUTOR, ETC**


