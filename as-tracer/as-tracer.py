#! /usr/local/bin/python3

from argparse import ArgumentParser
import subprocess
import re
import requests

parser = ArgumentParser(description='')
parser.add_argument('--addr', type=str, help='')

args = parser.parse_args().__dict__

traceroute = f'traceroute -q 1 {args["addr"]}'

traceroute_res = subprocess.check_output(traceroute, shell=True)
print(traceroute_res)
ip_regex = r'd{1,3}.d{1,3}.d{1,3}.d{1,3}'
ips = re.findall(ip_regex, traceroute_res)
print(ips)

api_url = 'https://ipinfo.io/{}/json'


def ip_info(ip):
    response = requests.get(api_url.format(ip))
    print(response)


for e in ips:
    print(f'id {e}')
