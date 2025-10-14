#!/bin/bash

git remote add pixels-origin git@github.com:pixelsdb/pixels.git

git fetch pixels-origin

git checkout pixels-origin/master -- cpp

# 遍历当前目录下的所有文件和文件夹
for item in *; do
    # 检查是否存在 cpp/ 对应的文件或文件夹
    if [ -e "cpp/$item" ]; then
        echo "Found matching item in cpp/: $item"
        
        # 判断是文件还是目录
        if [ -d "$item" ]; then
            # 如果是目录，递归复制 cpp/ 目录下的内容到当前目录下的对应目录
            echo "Syncing directory: $item"
            cp -r cpp/"$item"/* "$item"/
        elif [ -f "$item" ]; then
            # 如果是文件，直接覆盖当前目录的文件
            echo "Syncing file: $item"
            cp cpp/"$item" "$item"
        fi
    else
        echo "No matching item in cpp/ for: $item"
    fi
done

rm -rf cpp

git add .

git commit -m "Auto-synced cpp directory from pixels repo"


git push origin master
