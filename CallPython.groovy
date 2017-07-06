
// Psedo code for python command
def process = ['cmd', '/c', 'filepathToPython.exe', 'filepathToPythonFile.py'].execute()
process.waitfor()
return p.text 