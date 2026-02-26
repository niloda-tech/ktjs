package cli

actual fun platformArgs(): Array<String> {
    val argv: Array<String> = js("typeof process !== 'undefined' ? process.argv : []")
    return argv.drop(2).toTypedArray()
}

actual fun readStdinLine(): String? {
    // Use raw JS try/catch so no exception can escape. On macOS/Node, readSync(0) often returns 0
    // or EAGAIN for pipes; readFileSync('/dev/stdin') works for piped input (reads until EOF).
    return js(
        """
        (function() {
            var proc = (typeof globalThis !== 'undefined' && globalThis.process) ? globalThis.process : null;
            if (!proc || !proc.stdin) return null;
            if (proc.stdin.isTTY === true) return null;
            try {
                var fs = require('fs');
                var s;
                try {
                    s = fs.readFileSync('/dev/stdin', 'utf8');
                } catch (e) {
                    var fd = (proc.stdin.fd !== undefined) ? proc.stdin.fd : 0;
                    var buf = Buffer.alloc(4096);
                    var n = fs.readSync(fd, buf, 0, 4096);
                    if (n <= 0) return null;
                    s = buf.slice(0, n).toString('utf8');
                }
                var first = (s && s.trim().split('\n')[0]);
                return (first && first.trim()) || null;
            } catch (e) {
                return null;
            }
        })()
        """
    ).unsafeCast<String?>()
}
