
import os

alpha = raw_input()

instances = ['qbf400']

	
for x in instances:
	print "Iniciou a instacia " + x + " com a variacao Reactive_GRASP_QBFPT "
	os.system("java -jar Reactive_GRASP_QBFPT.jar  " +x + " > Reactive_GRASP_QBFPT/"+x+".txt")
	print "Terminou a instancia " + x + "com a variacao: Reactive_GRASP_QBFPT " 
