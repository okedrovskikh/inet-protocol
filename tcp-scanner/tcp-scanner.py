#! /usr/bin/python
import socket
from argparse import ArgumentParser

parser = ArgumentParser(description='tcp scanner, looks for open ports')
parser.add_argument('--addr', type=str, help='ip или доменное имя', default='localhost')
parser.add_argument('--bottom', type=int, help='нижнее значение портов', default=1)
parser.add_argument('--top', type=int, help='верхнее значение портов', default=65535)

args = parser.parse_args().__dict__

addr = socket.gethostbyname(args['addr'])
ports_range = (args['bottom'], args['top'])

for port in range(ports_range[0], ports_range[1]):
    sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    socket.setdefaulttimeout(10)
    res = sock.connect_ex((addr, port))
    if res == 0:
        print(f'Доступен порт: {port}')
    sock.close()
