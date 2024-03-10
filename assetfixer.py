# This script takes the existing MinecraftResources XML file and adds the hash to it as we don't have access to a json library
import xml.etree.ElementTree as ET
from xml.etree.ElementTree import ElementTree
import requests
import json

a = requests.get("https://web.archive.org/web/20120729205914id_/https://s3.amazonaws.com/MinecraftResources/")
root = ET.fromstring(a.text)
a = requests.get("https://launchermeta.mojang.com/v1/packages/3d8e55480977e32acd9844e545177e69a52f594b/pre-1.6.json")
parsed = json.loads(a.text)
for child in root:
    print(child.tag)
    print(child.attrib)
    if child.tag == "{http://s3.amazonaws.com/doc/2006-03-01/}Contents":
        print(child.find('{http://s3.amazonaws.com/doc/2006-03-01/}Key').text)
        try:
            a = str(parsed["objects"][str(child.find('{http://s3.amazonaws.com/doc/2006-03-01/}Key').text)]["hash"])
            print(a)
            #child.append(new)
        except Exception as e:
            a = " "
            print("Nonexistant key!", e)
        new = ET.SubElement(child,'Hash')
        new.text = a
tree = ElementTree(root)
with open('output.xml', 'w') as f:
    tree.write(f, encoding='unicode')
    
a = open('output.xml','r')
dat = a.read()
a.close()
a = open('output.xml','w')
a.write(dat.replace('ns0:','').replace(':ns0',''))
a.close()