#!/usr/bin/env python3
"""Minimal RCON-klient for server-røyktesting."""
import socket
import struct
import sys


class Rcon:
    def __init__(self, host="127.0.0.1", port=25575, password="test123"):
        self.sock = socket.create_connection((host, port), timeout=10)
        self.req_id = 0
        self._send(3, password)

    def _send(self, ptype, payload):
        self.req_id += 1
        data = struct.pack("<ii", self.req_id, ptype) + payload.encode() + b"\x00\x00"
        self.sock.sendall(struct.pack("<i", len(data)) + data)
        return self._recv()

    def _recv(self):
        length = struct.unpack("<i", self._read(4))[0]
        data = self._read(length)
        rid, rtype = struct.unpack("<ii", data[:8])
        return rid, data[8:-2].decode(errors="replace")

    def _read(self, n):
        buf = b""
        while len(buf) < n:
            chunk = self.sock.recv(n - len(buf))
            if not chunk:
                raise ConnectionError("closed")
            buf += chunk
        return buf

    def cmd(self, command):
        return self._send(2, command)[1]


if __name__ == "__main__":
    r = Rcon()
    for command in sys.argv[1:]:
        print(f"> {command}")
        print(r.cmd(command))
