# 实验1：部署mini-pixels环境并进行TPCH测试  

---

**发布时间**：2024年10月15日
**截止时间**：2024年10月29日下午1点
**提交方式**：OBE平台
**负责人**：王浩哲，尹佳

---

## 1. 实验概述  

本实验旨在熟悉如何部署`mini-pixels`开发环境，按照文档编译与运行查询。通过本次实验，你将掌握Linux环境下软件环境的部署、数据集的操作及基本测试方法。  

---

## 2. 实验步骤  

**注：本实验需在Linux环境下进行**  

1. **fork项目并clone代码**  
   项目链接：[https://github.com/pixelsdb/mini-pixels](https://github.com/pixelsdb/mini-pixels)  
   在GitHub上fork项目，并将仓库clone至本地：  

   ```bash
   git clone https://github.com/your_username/mini-pixels.git
   cd mini-pixels
   ```

   [若不清楚如何fork，请参考GitHub官方文档](https://docs.github.com/en/pull-requests/collaborating-with-pull-requests/working-with-forks/fork-a-repo)。  

2. **拉取submodule**  
   进入`mini-pixels`目录，并拉取项目的submodule（此步骤可能需要几分钟）：  

   ```bash
   make pull
   ```

3. **配置环境变量**  

   ```bash
   export PIXELS_HOME=$(pwd)
   export PIXELS_SRC=$(pwd)
   ```

4. **编译代码**  

   ```bash
   make -j$(nproc)
   ```

5. **下载测试数据并解压**  
   将数据包从服务器下载并解压：  

   ```bash
   wget http://10.77.110.75/pixels/pixels-tpch-1.zip
   unzip pixels-tpch-1.zip
   ```
   
6. **修改测试数据路径**  
   使用`vim`编辑路径，并进行全局替换：  

   ```bash
   cd pixels-duckdb/benchmark/tpch/pixels/
   vim pixels_tpch_template.benchmark.in
   # 使用以下命令全局替换路径：
   :%s#/data/9a3-02/tpch-1#/home/pixels/about-class/mini-pixels#g
   ```

   **提示**:请将`/home/pixels/about-class/mini-pixels`替换为实际路径。

7. **运行pixels reader测试并截屏**  

   ```bash
   cd $PIXELS_SRC
   ./build/release/examples/pixels-example/pixels-example
   ```

8. **进行TPCH测试并截屏**  

   ```bash
   cd pixels-duckdb
   python run_benchmark_simple.py --dir benchmark/tpch/pixels/tpch_1/
   cat output/pixels_tpch_1.csv
   ```

---

## 3. 提交要求  

1. **实验报告**  
   请参考实验报告模板撰写本次实验报告，报告应包括但不限于：  
   - 实验描述  
   - 实验过程  
   - 实验结果（包括测试截图）  
   - 遇到的问题及解决方案  
   
   **篇幅要求**：请将内容控制在**2页内**。

2. **提交方式**  
   请将实验报告按时上传至OBE平台。  
   - **截止时间**：2024年10月29日下午1点  
   - **迟交政策**：迟交一周内标记为超时，一周后不再接收。

---

## 4. 附件示例  

实验结果截图参考experiments/lab1-ref.png
