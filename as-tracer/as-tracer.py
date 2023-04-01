#! /usr/bin/python

from argparse import ArgumentParser
import subprocess
import re
import requests

parser = ArgumentParser(description='')
parser.add_argument('--addr', type=str, help='')

args = parser.parse_args().__dict__

traceroute = f'traceroute {args["addr"]}'

traceroute_res = subprocess.check_output(traceroute, shell=True)
ip_regex = re.compile(r'd{1,3}.d{1,3}.d{1,3}.d{1,3}')
ips = ip_regex.findall(traceroute_res)

api_url = 'https://ipinfo.io/{}/json'


def ip_info(ip):
    response = requests.get(api_url.format(ip))
    print(response)


for e in ips:
    print(f'id {e}')
