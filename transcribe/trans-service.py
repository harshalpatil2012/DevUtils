import win32serviceutil
import win32service
import win32event
import win32api
import subprocess
import time

class MyService(win32serviceutil.ServiceFramework):
    _svc_name_ = 'MyPythonService'
    _svc_display_name_ = 'Trans_Service'
    _svc_description_ = 'A Python service that runs in the background'

    def __init__(self, args):
        win32serviceutil.ServiceFramework.__init__(self, args)
        self.hWaitStop = win32event.CreateEvent(None, 0, 0, None)
        # Update the path to your Python script
        script_path = 'D:/GitHubProjects/DevUtils/transcribe/transcribe.py'
        python_path = 'C:/Users/harsh/AppData/Local/Programs/Python/Python311/python.exe'  # Update this to your Python executable path if necessary
        self.process = subprocess.Popen([python_path, script_path], cwd='D:/GitHubProjects/DevUtils/transcribe')

    def SvcStop(self):
        self.ReportServiceStatus(win32service.SERVICE_STOP_PENDING)
        self.process.terminate()
        win32event.SetEvent(self.hWaitStop)

    def SvcDoRun(self):
        while True:
            # Your main service loop code
            time.sleep(10)

if __name__ == '__main__':
    win32serviceutil.HandleCommandLine(MyService)
