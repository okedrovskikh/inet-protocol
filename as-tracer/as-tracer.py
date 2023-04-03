#! /usr/bin/python
from argparse import ArgumentParser
import subprocess
import re
import requests

parser = ArgumentParser(description='Выводит as, страну, провайдера по ip или доменному имени')
parser.add_argument('--addr', type=str, help='ip или доменное имя')

args = parser.parse_args().__dict__

table_str = '{:<7}|{:<8}|{:<20}|{:<20}|{:<20}|{:<20}'
table_header = table_str.format('ordinal', 'is white', 'ip', 'as', 'country', 'provider')

traceroute = f'traceroute {args["addr"]}'

traceroute_res = subprocess.check_output(traceroute, shell=True).decode('utf-8')
print(traceroute_res)
ip_regex = re.compile(r'\((\d{1,3}.\d{1,3}.\d{1,3}.\d{1,3})\)')
ips = ip_regex.findall(traceroute_res)


class IpInfo:
    _api_url = 'http://ip-api.com/json/{}'

    def __init__(self, ip):
        global counter
        self._ordinal = counter
        counter += 1
        self._ip = ip
        self._white = '*'
        self._as = '*'
        self._provider = '*'
        self._country = '*'

    def enrich(self):
        response = requests.get(self._api_url.format(ip))
        if response.status_code == 200:
            response_json = response.json()
            if response_json['status'] == 'fail':
                self._white = False
            else:
                self._white = True
                self._country = response_json['country']
                asn = response_json['as']
                if asn == '':
                    self._as = 'api no info'
                else:
                    self._as = asn
                self._provider = response_json['isp']
        return self

    def __str__(self):
        return table_str.format(self._ordinal, self._white, self._ip, self._as, self._country, self._provider)


extended_ip = []
counter = 1
for ip in ips:
    extended_ip.append(IpInfo(ip).enrich())

print(table_header)
for e in extended_ip:
    print(e.__str__())
