
import os

alpha = raw_input()

instances = ['qbf020','qbf040','qbf060','qbf080','qbf100','qbf200','qbf400']
variacao = ['Pop2_GRASP_QBFPT']

	
for v in variacao:
	for x in instances:
		print "Iniciou a instacia " + x + " com a variacao: " + v
		os.system("java -jar "+ v + ".jar " + alpha + " " +x + " > "+ v +"/"+x+"_"+alpha+".txt")
		print "Terminou a instancia " + x + "com a variacao: " + v
		

