from xml.etree import ElementTree as ET
path = r'c:\Users\udaya\Desktop\cyber-asset-discovery-scanner\backend\pom.xml'
try:
    ET.parse(path)
    print('OK')
except ET.ParseError as e:
    print('PARSE ERROR', e)
