# Build
```bash
docker build -t ccp .
```
# Run
```bash
docker run -d --name cco -p 8080:8080 --restart=always ccp
```
# Open browser
http://127.0.0.1:8080/
