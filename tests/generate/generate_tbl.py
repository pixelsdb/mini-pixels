#!/usr/bin/env python3

"""
Script: generate_tbl.py
功能: 根据提供的schema字符串生成符合TPC-H格式的.tbl文件。每条记录的各个字段通过'|'分隔，并根据TypeDescription类中定义的所有类型生成相应的随机数据（不包括STRUCT类型）。

使用方法:
    python generate_tbl.py "<type1, type2, ...>" output.tbl [num_records]

参数说明:
    1. "<type1, type2, ...>": 以尖括号包围的类型列表，可以包含带参数的类型。例如:
        - 基本类型: <int, long, timestamp>
        - 带参数的类型: <decimal(10,2), varchar(50), date>
    2. output.tbl: 生成的.tbl文件名。
    3. [num_records]: 可选参数，指定生成的记录行数。默认为100。

支持的类型:
    - 基本类型:
        BOOLEAN
        BYTE
        SHORT
        INT
        LONG
        FLOAT
        DOUBLE
        DECIMAL(precision, scale)
        STRING
        DATE
        TIME
        TIMESTAMP
        VARBINARY
        BINARY
        VARCHAR(max_length)
        CHAR(length)

示例用法:
    1. 生成包含int, long, timestamp类型的100条记录:
        python generate_tbl.py "<int, long, timestamp>" output.tbl

    2. 生成包含decimal(10,2), varchar(50), date类型的200条记录:
        python generate_tbl.py "<decimal(10,2), varchar(50), date>" decimal_varchar_date.tbl 200

    3. 生成包含char(10), boolean, float类型的150条记录:
        python generate_tbl.py "<char(10), boolean, float>" char_bool_float.tbl 150

注意事项:
    - 类型名称不区分大小写，例如'int'和'INT'等效。
    - 对于带参数的类型，如DECIMAL、VARCHAR、CHAR，参数必须正确指定。例如，DECIMAL需要precision和scale，且precision必须大于scale。
    - 如果schema格式不正确或包含不支持的类型，脚本将输出错误信息并退出。
    - 生成的值如果包含分隔符'|'或双引号'"'，将自动用双引号包裹，并将内部的双引号转义为两个双引号，以确保.tbl文件格式正确。
    - 确保脚本具有执行权限，并使用合适的Python解释器运行（推荐Python 3.6及以上版本）。
"""

import sys
import random
import string
import json
from datetime import datetime, timedelta
import re

def generate_boolean():
    return random.choice(['true', 'false'])

def generate_byte():
    return str(random.randint(0, 255))  # BYTE范围: 0-255

def generate_short():
    return str(random.randint(-32768, 32767))  # SHORT范围: -32768 到 32767

def generate_int():
    return str(random.randint(0, 2147483647))  # INT范围: -2,147,483,648 到 2,147,483,647

def generate_long():
    return str(random.randint(-9223372036854775808, 9223372036854775807))  # LONG范围: -9,223,372,036,854,775,808 到 9,223,372,036,854,775,807

def generate_float():
    return f"{random.uniform(-3.4e38, 3.4e38):.6f}"  # FLOAT范围: 大约 -3.4E38 到 3.4E38

def generate_double():
    return f"{random.uniform(-1.7e308, 1.7e308):.6f}"  # DOUBLE范围: 大约 -1.7E308 到 1.7E308

def generate_decimal(precision=10, scale=2):
    if precision <= scale:
        raise ValueError("Precision must be greater than scale for DECIMAL type.")
    max_int = 10**(precision - scale) - 1
    int_part = random.randint(0, max_int)
    frac_part = random.randint(0, 10**scale - 1)
    return f"{int_part}.{frac_part:0{scale}d}"

def generate_string():
    length = random.randint(5, 14)
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))

def generate_date():
    start_date = datetime(1990, 1, 1)
    end_date = datetime(2025, 12, 31)
    delta = end_date - start_date
    random_days = random.randint(0, delta.days)
    return (start_date + timedelta(days=random_days)).strftime('%Y-%m-%d')

def generate_time():
    hour = random.randint(0, 23)
    minute = random.randint(0, 59)
    second = random.randint(0, 59)
    return f"{hour:02}:{minute:02}:{second:02}"

def generate_timestamp():
    start_date = datetime(1990, 1, 1, 0, 0, 0)
    end_date = datetime(2025, 12, 31, 23, 59, 59)
    delta = end_date - start_date
    random_seconds = random.randint(0, int(delta.total_seconds()))
    return (start_date + timedelta(seconds=random_seconds)).strftime('%Y-%m-%d %H:%M:%S')

def generate_varbinary():
    length = random.randint(5, 20)
    return ''.join(random.choices('0123456789ABCDEF', k=length))  # 十六进制表示

def generate_binary():
    length = random.randint(5, 20)
    return ''.join(random.choices('0123456789ABCDEF', k=length))  # 十六进制表示

def generate_varchar(max_length=50):
    length = random.randint(1, max_length)
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))

def generate_char(length=10):
    return ''.join(random.choices(string.ascii_letters + string.digits, k=length))

def generate_value(type_def):
    # type_def 可能包含参数，如 decimal(10,2), varchar(50), char(10)
    type_def = type_def.strip()
    match = re.match(r'^(\w+)(\(([^)]+)\))?$', type_def)
    if not match:
        raise ValueError(f"Invalid type definition: {type_def}")

    base_type = match.group(1).lower()
    params = match.group(3)

    if base_type == 'boolean':
        return generate_boolean()
    elif base_type == 'byte':
        return generate_byte()
    elif base_type == 'short':
        return generate_short()
    elif base_type == 'int':
        return generate_int()
    elif base_type == 'long':
        return generate_long()
    elif base_type == 'float':
        return generate_float()
    elif base_type == 'double':
        return generate_double()
    elif base_type == 'decimal':
        if params:
            precision_scale = params.split(',')
            if len(precision_scale) != 2:
                raise ValueError(f"DECIMAL type requires precision and scale, got: {params}")
            precision, scale = map(int, precision_scale)
        else:
            precision, scale = 10, 2  # 默认值
        return generate_decimal(precision, scale)
    elif base_type == 'string':
        return generate_string()
    elif base_type == 'date':
        return generate_date()
    elif base_type == 'time':
        return generate_time()
    elif base_type == 'timestamp':
        return generate_timestamp()
    elif base_type == 'varbinary':
        return generate_varbinary()
    elif base_type == 'binary':
        return generate_binary()
    elif base_type == 'varchar':
        max_length = int(params) if params and params.isdigit() else 50
        return generate_varchar(max_length)
    elif base_type == 'char':
        length = int(params) if params and params.isdigit() else 10
        return generate_char(length)
    else:
        raise ValueError(f"Unsupported type: {base_type}")

def parse_schema(schema_str):
    # 移除尖括号和空格
    schema_str = schema_str.strip('<>').replace(' ', '')
    types = []
    current = ''
    depth = 0
    for c in schema_str:
        if c in ['<', '(']:
            depth +=1
            current +=c
        elif c in ['>', ')']:
            depth -=1
            current +=c
        elif c == ',' and depth ==0:
            types.append(current)
            current = ''
        else:
            current +=c
    if current:
        types.append(current)
    return types

def main():
    if len(sys.argv) < 3 or len(sys.argv) > 4:
        print("Usage: python generate_tbl.py \"<type1, type2, ...>\" output.tbl [num_records]")
        sys.exit(1)

    schema = sys.argv[1]
    output_file = sys.argv[2]
    try:
        num_records = int(sys.argv[3]) if len(sys.argv) == 4 else 100
        if num_records <= 0:
            raise ValueError
    except ValueError:
        print("Error: num_records must be a positive integer.")
        sys.exit(1)

    try:
        types = parse_schema(schema)
    except Exception as e:
        print(f"Error parsing schema: {e}")
        sys.exit(1)

    # 验证所有类型
    supported_types = {'boolean', 'byte', 'short', 'int', 'long', 'float', 'double',
                       'decimal', 'string', 'date', 'time', 'timestamp',
                       'varbinary', 'binary', 'varchar', 'char'}
    for type_def in types:
        base_type_match = re.match(r'^(\w+)', type_def.strip())
        if not base_type_match or base_type_match.group(1).lower() not in supported_types:
            print(f"Error: Unsupported or invalid type in schema: {type_def}")
            sys.exit(1)

    try:
        with open(output_file, 'w') as f:
            for _ in range(num_records):
                record = []
                for type_def in types:
                    value = generate_value(type_def)
                    # 处理包含特殊字符的值（如包含|的字符串），这里简单处理为用双引号包裹
                    if '|' in value or '"' in value:
                        value = '"' + value.replace('"', '""') + '"'
                    record.append(value)
                f.write('|'.join(record) + '\n')
    except Exception as e:
        print(f"Error generating data: {e}")
        sys.exit(1)

    print(f"成功生成 {output_file}，记录数量: {num_records}")

if __name__ == "__main__":
    main()
