
import os

alpha = raw_input()

instances = ['qbf400']
variacao = ['GRASP_QBFPT', 'Pop_GRASP_QBFPT']

	
for v in variacao:
	for x in instances:
		print "Iniciou a instacia " + x + " com a variacao: " + v
		os.system("java -jar "+ v + ".jar " + alpha + " " +x + " > "+ v +"/"+x+"_"+alpha+".txt")
		print "Terminou a instancia " + x + "com a variacao: " + v
		
for x in instances:
	print "Iniciou a instacia " + x + " com a variacao Reactive_GRASP_QBFPT "
	os.system("java -jar Reactive_GRASP_QBFPT.jar  " +x + " > Reactive_GRASP_QBFPT/"+x+".txt")
	print "Terminou a instancia " + x + "com a variacao: Reactive_GRASP_QBFPT " 
	
alpha = "0.10"

for x in instances:
	print "Iniciou a instacia " + x + " com a variacao: GRASP_QBFPT " 
	os.system("java -jar GRASP_QBFPT.jar " + alpha + " " +x + " > GRASP_QBFPT/"+x+"_"+alpha+".txt")
	print "Terminou a instancia " + x + "com a variacao: GRASP_QBFPT"
print("FIm")
