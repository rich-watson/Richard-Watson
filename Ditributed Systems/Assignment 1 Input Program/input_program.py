import sys
import getopt

argv = sys.argv[1:]
opt1 = None
opt2 = None
opt3 = None


try:
    opts, args = getopt.getopt(argv, "o:t:h")
    
except:
    print('error')

for opt, arg in opts:
    if opt in ['-o']:
        opt1 = arg
    elif opt in ['-t']:
        opt2 = arg
    elif opt in ['-h']:
        opt3 = ''


print("Standard Input: ")

text = sys.stdin.readline()
while text:
    print(text.strip('\n'))
    text = sys.stdin.readline()
print("Command line arguments:")
if opt1 != None:
    print("option 1: " + str(opt1))
if opt2 != None:
    print("option 2: " + str(opt2))
if opt3 != None:
    print("option 3"+ str(opt3))
