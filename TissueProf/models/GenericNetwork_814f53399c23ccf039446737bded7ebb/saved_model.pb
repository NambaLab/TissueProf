
еЊ
:
Add
x"T
y"T
z"T"
Ttype:
2	
B
AssignVariableOp
resource
value"dtype"
dtypetype
~
BiasAdd

value"T	
bias"T
output"T" 
Ttype:
2	"-
data_formatstringNHWC:
NHWCNCHW
h
ConcatV2
values"T*N
axis"Tidx
output"T"
Nint(0"	
Ttype"
Tidxtype0:
2	
8
Const
output"dtype"
valuetensor"
dtypetype

Conv2D

input"T
filter"T
output"T"
Ttype:
2"
strides	list(int)"
use_cudnn_on_gpubool(",
paddingstring:
SAMEVALIDEXPLICIT""
explicit_paddings	list(int)
 "-
data_formatstringNHWC:
NHWCNCHW" 
	dilations	list(int)

П
Conv2DBackpropInput
input_sizes
filter"T
out_backprop"T
output"T"
Ttype:
2"
strides	list(int)"
use_cudnn_on_gpubool(",
paddingstring:
SAMEVALIDEXPLICIT""
explicit_paddings	list(int)
 "-
data_formatstringNHWC:
NHWCNCHW" 
	dilations	list(int)

.
Identity

input"T
output"T"	
Ttype
д
MaxPool

input"T
output"T"
Ttype0:
2	"
ksize	list(int)(0"
strides	list(int)(0""
paddingstring:
SAMEVALID":
data_formatstringNHWC:
NHWCNCHWNCHW_VECT_C
e
MergeV2Checkpoints
checkpoint_prefixes
destination_prefix"
delete_old_dirsbool(
=
Mul
x"T
y"T
z"T"
Ttype:
2	

NoOp
M
Pack
values"T*N
output"T"
Nint(0"	
Ttype"
axisint 
C
Placeholder
output"dtype"
dtypetype"
shapeshape:
X
PlaceholderWithDefault
input"dtype
output"dtype"
dtypetype"
shapeshape
~
RandomUniform

shape"T
output"dtype"
seedint "
seed2int "
dtypetype:
2"
Ttype:
2	
@
ReadVariableOp
resource
value"dtype"
dtypetype
E
Relu
features"T
activations"T"
Ttype:
2	

ResizeNearestNeighbor
images"T
size
resized_images"T"
Ttype:
2		"
align_cornersbool( "
half_pixel_centersbool( 
o
	RestoreV2

prefix
tensor_names
shape_and_slices
tensors2dtypes"
dtypes
list(type)(0
l
SaveV2

prefix
tensor_names
shape_and_slices
tensors2dtypes"
dtypes
list(type)(0
P
Shape

input"T
output"out_type"	
Ttype"
out_typetype0:
2	
H
ShardedFilename
basename	
shard

num_shards
filename
0
Sigmoid
x"T
y"T"
Ttype:

2
і
StridedSlice

input"T
begin"Index
end"Index
strides"Index
output"T"	
Ttype"
Indextype:
2	"

begin_maskint "
end_maskint "
ellipsis_maskint "
new_axis_maskint "
shrink_axis_maskint 
N

StringJoin
inputs*N

output"
Nint(0"
	separatorstring 
:
Sub
x"T
y"T
z"T"
Ttype:
2	
q
VarHandleOp
resource"
	containerstring "
shared_namestring "
dtypetype"
shapeshape
9
VarIsInitializedOp
resource
is_initialized
"serve*1.15.52v1.15.4-39-g3db52be7be8Х

inputPlaceholder*6
shape-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0
v
conv2d_1/random_uniform/shapeConst*
_output_shapes
:*
dtype0*%
valueB"             
`
conv2d_1/random_uniform/minConst*
dtype0*
_output_shapes
: *
valueB
 *О
`
conv2d_1/random_uniform/maxConst*
_output_shapes
: *
valueB
 *>*
dtype0
В
%conv2d_1/random_uniform/RandomUniformRandomUniformconv2d_1/random_uniform/shape*&
_output_shapes
: *
dtype0*
seedБџх)*
seed2ЯЇ*
T0
}
conv2d_1/random_uniform/subSubconv2d_1/random_uniform/maxconv2d_1/random_uniform/min*
T0*
_output_shapes
: 

conv2d_1/random_uniform/mulMul%conv2d_1/random_uniform/RandomUniformconv2d_1/random_uniform/sub*
T0*&
_output_shapes
: 

conv2d_1/random_uniformAddconv2d_1/random_uniform/mulconv2d_1/random_uniform/min*
T0*&
_output_shapes
: 
З
conv2d_1/kernelVarHandleOp* 
shared_nameconv2d_1/kernel*
	container *
shape: *
dtype0*"
_class
loc:@conv2d_1/kernel*
_output_shapes
: 
o
0conv2d_1/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpconv2d_1/kernel*
_output_shapes
: 
a
conv2d_1/kernel/AssignAssignVariableOpconv2d_1/kernelconv2d_1/random_uniform*
dtype0
{
#conv2d_1/kernel/Read/ReadVariableOpReadVariableOpconv2d_1/kernel*&
_output_shapes
: *
dtype0
[
conv2d_1/ConstConst*
_output_shapes
: *
valueB *    *
dtype0
Ѕ
conv2d_1/biasVarHandleOp* 
_class
loc:@conv2d_1/bias*
shared_nameconv2d_1/bias*
	container *
_output_shapes
: *
shape: *
dtype0
k
.conv2d_1/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpconv2d_1/bias*
_output_shapes
: 
T
conv2d_1/bias/AssignAssignVariableOpconv2d_1/biasconv2d_1/Const*
dtype0
k
!conv2d_1/bias/Read/ReadVariableOpReadVariableOpconv2d_1/bias*
_output_shapes
: *
dtype0
{
#conv2d_1/convolution/ReadVariableOpReadVariableOpconv2d_1/kernel*
dtype0*&
_output_shapes
: 

conv2d_1/convolutionConv2Dinput#conv2d_1/convolution/ReadVariableOp*
T0*
use_cudnn_on_gpu(*
strides
*
explicit_paddings
 *
paddingSAME*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
	dilations
*
data_formatNHWC
i
conv2d_1/BiasAdd/ReadVariableOpReadVariableOpconv2d_1/bias*
dtype0*
_output_shapes
: 
Е
conv2d_1/BiasAddBiasAddconv2d_1/convolutionconv2d_1/BiasAdd/ReadVariableOp*
data_formatNHWC*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 
s
conv2d_1/ReluReluconv2d_1/BiasAdd*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 
v
conv2d_2/random_uniform/shapeConst*
_output_shapes
:*%
valueB"              *
dtype0
`
conv2d_2/random_uniform/minConst*
dtype0*
valueB
 *ьбН*
_output_shapes
: 
`
conv2d_2/random_uniform/maxConst*
_output_shapes
: *
valueB
 *ьб=*
dtype0
В
%conv2d_2/random_uniform/RandomUniformRandomUniformconv2d_2/random_uniform/shape*
seed2ІИд*
T0*&
_output_shapes
:  *
dtype0*
seedБџх)
}
conv2d_2/random_uniform/subSubconv2d_2/random_uniform/maxconv2d_2/random_uniform/min*
T0*
_output_shapes
: 

conv2d_2/random_uniform/mulMul%conv2d_2/random_uniform/RandomUniformconv2d_2/random_uniform/sub*
T0*&
_output_shapes
:  

conv2d_2/random_uniformAddconv2d_2/random_uniform/mulconv2d_2/random_uniform/min*&
_output_shapes
:  *
T0
З
conv2d_2/kernelVarHandleOp*
	container *
_output_shapes
: *
dtype0*
shape:  *"
_class
loc:@conv2d_2/kernel* 
shared_nameconv2d_2/kernel
o
0conv2d_2/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpconv2d_2/kernel*
_output_shapes
: 
a
conv2d_2/kernel/AssignAssignVariableOpconv2d_2/kernelconv2d_2/random_uniform*
dtype0
{
#conv2d_2/kernel/Read/ReadVariableOpReadVariableOpconv2d_2/kernel*&
_output_shapes
:  *
dtype0
[
conv2d_2/ConstConst*
_output_shapes
: *
valueB *    *
dtype0
Ѕ
conv2d_2/biasVarHandleOp* 
_class
loc:@conv2d_2/bias*
	container *
dtype0*
shared_nameconv2d_2/bias*
_output_shapes
: *
shape: 
k
.conv2d_2/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpconv2d_2/bias*
_output_shapes
: 
T
conv2d_2/bias/AssignAssignVariableOpconv2d_2/biasconv2d_2/Const*
dtype0
k
!conv2d_2/bias/Read/ReadVariableOpReadVariableOpconv2d_2/bias*
dtype0*
_output_shapes
: 
{
#conv2d_2/convolution/ReadVariableOpReadVariableOpconv2d_2/kernel*&
_output_shapes
:  *
dtype0
Ѕ
conv2d_2/convolutionConv2Dconv2d_1/Relu#conv2d_2/convolution/ReadVariableOp*
	dilations
*
T0*
paddingSAME*
explicit_paddings
 *
strides
*
data_formatNHWC*
use_cudnn_on_gpu(*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 
i
conv2d_2/BiasAdd/ReadVariableOpReadVariableOpconv2d_2/bias*
_output_shapes
: *
dtype0
Е
conv2d_2/BiasAddBiasAddconv2d_2/convolutionconv2d_2/BiasAdd/ReadVariableOp*
T0*
data_formatNHWC*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 
s
conv2d_2/ReluReluconv2d_2/BiasAdd*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
T0
а
max_pooling2d_1/MaxPoolMaxPoolconv2d_2/Relu*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
strides
*
T0*
ksize
*
paddingVALID*
data_formatNHWC

&down_level_0_no_0/random_uniform/shapeConst*
dtype0*
_output_shapes
:*%
valueB"              
i
$down_level_0_no_0/random_uniform/minConst*
dtype0*
_output_shapes
: *
valueB
 *ьбН
i
$down_level_0_no_0/random_uniform/maxConst*
dtype0*
valueB
 *ьб=*
_output_shapes
: 
Ф
.down_level_0_no_0/random_uniform/RandomUniformRandomUniform&down_level_0_no_0/random_uniform/shape*&
_output_shapes
:  *
dtype0*
T0*
seedБџх)*
seed2Ѕаж

$down_level_0_no_0/random_uniform/subSub$down_level_0_no_0/random_uniform/max$down_level_0_no_0/random_uniform/min*
_output_shapes
: *
T0
В
$down_level_0_no_0/random_uniform/mulMul.down_level_0_no_0/random_uniform/RandomUniform$down_level_0_no_0/random_uniform/sub*
T0*&
_output_shapes
:  
Є
 down_level_0_no_0/random_uniformAdd$down_level_0_no_0/random_uniform/mul$down_level_0_no_0/random_uniform/min*&
_output_shapes
:  *
T0
в
down_level_0_no_0/kernelVarHandleOp*
_output_shapes
: *+
_class!
loc:@down_level_0_no_0/kernel*
dtype0*)
shared_namedown_level_0_no_0/kernel*
	container *
shape:  

9down_level_0_no_0/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_0_no_0/kernel*
_output_shapes
: 
|
down_level_0_no_0/kernel/AssignAssignVariableOpdown_level_0_no_0/kernel down_level_0_no_0/random_uniform*
dtype0

,down_level_0_no_0/kernel/Read/ReadVariableOpReadVariableOpdown_level_0_no_0/kernel*
dtype0*&
_output_shapes
:  
d
down_level_0_no_0/ConstConst*
dtype0*
_output_shapes
: *
valueB *    
Р
down_level_0_no_0/biasVarHandleOp*
dtype0*'
shared_namedown_level_0_no_0/bias*
_output_shapes
: *
	container *)
_class
loc:@down_level_0_no_0/bias*
shape: 
}
7down_level_0_no_0/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_0_no_0/bias*
_output_shapes
: 
o
down_level_0_no_0/bias/AssignAssignVariableOpdown_level_0_no_0/biasdown_level_0_no_0/Const*
dtype0
}
*down_level_0_no_0/bias/Read/ReadVariableOpReadVariableOpdown_level_0_no_0/bias*
_output_shapes
: *
dtype0

,down_level_0_no_0/convolution/ReadVariableOpReadVariableOpdown_level_0_no_0/kernel*
dtype0*&
_output_shapes
:  
С
down_level_0_no_0/convolutionConv2Dmax_pooling2d_1/MaxPool,down_level_0_no_0/convolution/ReadVariableOp*
data_formatNHWC*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
paddingSAME*
use_cudnn_on_gpu(*
T0*
	dilations
*
strides
*
explicit_paddings
 
{
(down_level_0_no_0/BiasAdd/ReadVariableOpReadVariableOpdown_level_0_no_0/bias*
_output_shapes
: *
dtype0
а
down_level_0_no_0/BiasAddBiasAdddown_level_0_no_0/convolution(down_level_0_no_0/BiasAdd/ReadVariableOp*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
T0*
data_formatNHWC

down_level_0_no_0/ReluReludown_level_0_no_0/BiasAdd*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
T0

&down_level_0_no_1/random_uniform/shapeConst*
_output_shapes
:*%
valueB"              *
dtype0
i
$down_level_0_no_1/random_uniform/minConst*
valueB
 *ьбН*
dtype0*
_output_shapes
: 
i
$down_level_0_no_1/random_uniform/maxConst*
_output_shapes
: *
dtype0*
valueB
 *ьб=
Ф
.down_level_0_no_1/random_uniform/RandomUniformRandomUniform&down_level_0_no_1/random_uniform/shape*
T0*
seedБџх)*
seed2рЗ*&
_output_shapes
:  *
dtype0

$down_level_0_no_1/random_uniform/subSub$down_level_0_no_1/random_uniform/max$down_level_0_no_1/random_uniform/min*
T0*
_output_shapes
: 
В
$down_level_0_no_1/random_uniform/mulMul.down_level_0_no_1/random_uniform/RandomUniform$down_level_0_no_1/random_uniform/sub*
T0*&
_output_shapes
:  
Є
 down_level_0_no_1/random_uniformAdd$down_level_0_no_1/random_uniform/mul$down_level_0_no_1/random_uniform/min*&
_output_shapes
:  *
T0
в
down_level_0_no_1/kernelVarHandleOp*
	container *
shape:  *
_output_shapes
: *+
_class!
loc:@down_level_0_no_1/kernel*)
shared_namedown_level_0_no_1/kernel*
dtype0

9down_level_0_no_1/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_0_no_1/kernel*
_output_shapes
: 
|
down_level_0_no_1/kernel/AssignAssignVariableOpdown_level_0_no_1/kernel down_level_0_no_1/random_uniform*
dtype0

,down_level_0_no_1/kernel/Read/ReadVariableOpReadVariableOpdown_level_0_no_1/kernel*
dtype0*&
_output_shapes
:  
d
down_level_0_no_1/ConstConst*
_output_shapes
: *
valueB *    *
dtype0
Р
down_level_0_no_1/biasVarHandleOp*
	container *
shape: *
_output_shapes
: *)
_class
loc:@down_level_0_no_1/bias*'
shared_namedown_level_0_no_1/bias*
dtype0
}
7down_level_0_no_1/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_0_no_1/bias*
_output_shapes
: 
o
down_level_0_no_1/bias/AssignAssignVariableOpdown_level_0_no_1/biasdown_level_0_no_1/Const*
dtype0
}
*down_level_0_no_1/bias/Read/ReadVariableOpReadVariableOpdown_level_0_no_1/bias*
_output_shapes
: *
dtype0

,down_level_0_no_1/convolution/ReadVariableOpReadVariableOpdown_level_0_no_1/kernel*&
_output_shapes
:  *
dtype0
Р
down_level_0_no_1/convolutionConv2Ddown_level_0_no_0/Relu,down_level_0_no_1/convolution/ReadVariableOp*
paddingSAME*
explicit_paddings
 *
strides
*
	dilations
*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
data_formatNHWC*
use_cudnn_on_gpu(*
T0
{
(down_level_0_no_1/BiasAdd/ReadVariableOpReadVariableOpdown_level_0_no_1/bias*
_output_shapes
: *
dtype0
а
down_level_0_no_1/BiasAddBiasAdddown_level_0_no_1/convolution(down_level_0_no_1/BiasAdd/ReadVariableOp*
data_formatNHWC*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
T0

down_level_0_no_1/ReluReludown_level_0_no_1/BiasAdd*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 
Я
max_0/MaxPoolMaxPooldown_level_0_no_1/Relu*
strides
*
T0*
paddingVALID*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
ksize
*
data_formatNHWC

&down_level_1_no_0/random_uniform/shapeConst*
_output_shapes
:*%
valueB"          @   *
dtype0
i
$down_level_1_no_0/random_uniform/minConst*
_output_shapes
: *
dtype0*
valueB
 *ЋЊЊН
i
$down_level_1_no_0/random_uniform/maxConst*
_output_shapes
: *
valueB
 *ЋЊЊ=*
dtype0
Ф
.down_level_1_no_0/random_uniform/RandomUniformRandomUniform&down_level_1_no_0/random_uniform/shape*
seedБџх)*
seed2Њ*&
_output_shapes
: @*
T0*
dtype0

$down_level_1_no_0/random_uniform/subSub$down_level_1_no_0/random_uniform/max$down_level_1_no_0/random_uniform/min*
_output_shapes
: *
T0
В
$down_level_1_no_0/random_uniform/mulMul.down_level_1_no_0/random_uniform/RandomUniform$down_level_1_no_0/random_uniform/sub*&
_output_shapes
: @*
T0
Є
 down_level_1_no_0/random_uniformAdd$down_level_1_no_0/random_uniform/mul$down_level_1_no_0/random_uniform/min*&
_output_shapes
: @*
T0
в
down_level_1_no_0/kernelVarHandleOp*)
shared_namedown_level_1_no_0/kernel*
dtype0*
_output_shapes
: *
shape: @*+
_class!
loc:@down_level_1_no_0/kernel*
	container 

9down_level_1_no_0/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_1_no_0/kernel*
_output_shapes
: 
|
down_level_1_no_0/kernel/AssignAssignVariableOpdown_level_1_no_0/kernel down_level_1_no_0/random_uniform*
dtype0

,down_level_1_no_0/kernel/Read/ReadVariableOpReadVariableOpdown_level_1_no_0/kernel*
dtype0*&
_output_shapes
: @
d
down_level_1_no_0/ConstConst*
valueB@*    *
dtype0*
_output_shapes
:@
Р
down_level_1_no_0/biasVarHandleOp*)
_class
loc:@down_level_1_no_0/bias*
	container *
_output_shapes
: *
dtype0*
shape:@*'
shared_namedown_level_1_no_0/bias
}
7down_level_1_no_0/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_1_no_0/bias*
_output_shapes
: 
o
down_level_1_no_0/bias/AssignAssignVariableOpdown_level_1_no_0/biasdown_level_1_no_0/Const*
dtype0
}
*down_level_1_no_0/bias/Read/ReadVariableOpReadVariableOpdown_level_1_no_0/bias*
dtype0*
_output_shapes
:@

,down_level_1_no_0/convolution/ReadVariableOpReadVariableOpdown_level_1_no_0/kernel*
dtype0*&
_output_shapes
: @
З
down_level_1_no_0/convolutionConv2Dmax_0/MaxPool,down_level_1_no_0/convolution/ReadVariableOp*
data_formatNHWC*
paddingSAME*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
T0*
use_cudnn_on_gpu(*
strides
*
	dilations
*
explicit_paddings
 
{
(down_level_1_no_0/BiasAdd/ReadVariableOpReadVariableOpdown_level_1_no_0/bias*
_output_shapes
:@*
dtype0
а
down_level_1_no_0/BiasAddBiasAdddown_level_1_no_0/convolution(down_level_1_no_0/BiasAdd/ReadVariableOp*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
data_formatNHWC

down_level_1_no_0/ReluReludown_level_1_no_0/BiasAdd*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
T0

&down_level_1_no_1/random_uniform/shapeConst*%
valueB"      @   @   *
_output_shapes
:*
dtype0
i
$down_level_1_no_1/random_uniform/minConst*
dtype0*
_output_shapes
: *
valueB
 *:ЭН
i
$down_level_1_no_1/random_uniform/maxConst*
_output_shapes
: *
dtype0*
valueB
 *:Э=
Ф
.down_level_1_no_1/random_uniform/RandomUniformRandomUniform&down_level_1_no_1/random_uniform/shape*
seedБџх)*
T0*&
_output_shapes
:@@*
seed2ОЯш*
dtype0

$down_level_1_no_1/random_uniform/subSub$down_level_1_no_1/random_uniform/max$down_level_1_no_1/random_uniform/min*
_output_shapes
: *
T0
В
$down_level_1_no_1/random_uniform/mulMul.down_level_1_no_1/random_uniform/RandomUniform$down_level_1_no_1/random_uniform/sub*&
_output_shapes
:@@*
T0
Є
 down_level_1_no_1/random_uniformAdd$down_level_1_no_1/random_uniform/mul$down_level_1_no_1/random_uniform/min*
T0*&
_output_shapes
:@@
в
down_level_1_no_1/kernelVarHandleOp*+
_class!
loc:@down_level_1_no_1/kernel*
shape:@@*
	container *
_output_shapes
: *
dtype0*)
shared_namedown_level_1_no_1/kernel

9down_level_1_no_1/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_1_no_1/kernel*
_output_shapes
: 
|
down_level_1_no_1/kernel/AssignAssignVariableOpdown_level_1_no_1/kernel down_level_1_no_1/random_uniform*
dtype0

,down_level_1_no_1/kernel/Read/ReadVariableOpReadVariableOpdown_level_1_no_1/kernel*
dtype0*&
_output_shapes
:@@
d
down_level_1_no_1/ConstConst*
dtype0*
valueB@*    *
_output_shapes
:@
Р
down_level_1_no_1/biasVarHandleOp*
dtype0*'
shared_namedown_level_1_no_1/bias*
_output_shapes
: *
	container *)
_class
loc:@down_level_1_no_1/bias*
shape:@
}
7down_level_1_no_1/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_1_no_1/bias*
_output_shapes
: 
o
down_level_1_no_1/bias/AssignAssignVariableOpdown_level_1_no_1/biasdown_level_1_no_1/Const*
dtype0
}
*down_level_1_no_1/bias/Read/ReadVariableOpReadVariableOpdown_level_1_no_1/bias*
dtype0*
_output_shapes
:@

,down_level_1_no_1/convolution/ReadVariableOpReadVariableOpdown_level_1_no_1/kernel*
dtype0*&
_output_shapes
:@@
Р
down_level_1_no_1/convolutionConv2Ddown_level_1_no_0/Relu,down_level_1_no_1/convolution/ReadVariableOp*
use_cudnn_on_gpu(*
paddingSAME*
	dilations
*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
strides
*
explicit_paddings
 *
data_formatNHWC*
T0
{
(down_level_1_no_1/BiasAdd/ReadVariableOpReadVariableOpdown_level_1_no_1/bias*
dtype0*
_output_shapes
:@
а
down_level_1_no_1/BiasAddBiasAdddown_level_1_no_1/convolution(down_level_1_no_1/BiasAdd/ReadVariableOp*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
data_formatNHWC*
T0

down_level_1_no_1/ReluReludown_level_1_no_1/BiasAdd*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@
Я
max_1/MaxPoolMaxPooldown_level_1_no_1/Relu*
paddingVALID*
T0*
ksize
*
data_formatNHWC*
strides
*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@

&down_level_2_no_0/random_uniform/shapeConst*
dtype0*%
valueB"      @      *
_output_shapes
:
i
$down_level_2_no_0/random_uniform/minConst*
valueB
 *я[qН*
dtype0*
_output_shapes
: 
i
$down_level_2_no_0/random_uniform/maxConst*
valueB
 *я[q=*
_output_shapes
: *
dtype0
Ф
.down_level_2_no_0/random_uniform/RandomUniformRandomUniform&down_level_2_no_0/random_uniform/shape*
T0*
seed2мІl*
seedБџх)*
dtype0*'
_output_shapes
:@

$down_level_2_no_0/random_uniform/subSub$down_level_2_no_0/random_uniform/max$down_level_2_no_0/random_uniform/min*
T0*
_output_shapes
: 
Г
$down_level_2_no_0/random_uniform/mulMul.down_level_2_no_0/random_uniform/RandomUniform$down_level_2_no_0/random_uniform/sub*'
_output_shapes
:@*
T0
Ѕ
 down_level_2_no_0/random_uniformAdd$down_level_2_no_0/random_uniform/mul$down_level_2_no_0/random_uniform/min*'
_output_shapes
:@*
T0
г
down_level_2_no_0/kernelVarHandleOp*)
shared_namedown_level_2_no_0/kernel*+
_class!
loc:@down_level_2_no_0/kernel*
	container *
shape:@*
_output_shapes
: *
dtype0

9down_level_2_no_0/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_2_no_0/kernel*
_output_shapes
: 
|
down_level_2_no_0/kernel/AssignAssignVariableOpdown_level_2_no_0/kernel down_level_2_no_0/random_uniform*
dtype0

,down_level_2_no_0/kernel/Read/ReadVariableOpReadVariableOpdown_level_2_no_0/kernel*'
_output_shapes
:@*
dtype0
f
down_level_2_no_0/ConstConst*
_output_shapes	
:*
dtype0*
valueB*    
С
down_level_2_no_0/biasVarHandleOp*
dtype0*'
shared_namedown_level_2_no_0/bias*
_output_shapes
: *
	container *
shape:*)
_class
loc:@down_level_2_no_0/bias
}
7down_level_2_no_0/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_2_no_0/bias*
_output_shapes
: 
o
down_level_2_no_0/bias/AssignAssignVariableOpdown_level_2_no_0/biasdown_level_2_no_0/Const*
dtype0
~
*down_level_2_no_0/bias/Read/ReadVariableOpReadVariableOpdown_level_2_no_0/bias*
dtype0*
_output_shapes	
:

,down_level_2_no_0/convolution/ReadVariableOpReadVariableOpdown_level_2_no_0/kernel*'
_output_shapes
:@*
dtype0
И
down_level_2_no_0/convolutionConv2Dmax_1/MaxPool,down_level_2_no_0/convolution/ReadVariableOp*
use_cudnn_on_gpu(*
T0*
	dilations
*
strides
*
paddingSAME*
explicit_paddings
 *
data_formatNHWC*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ
|
(down_level_2_no_0/BiasAdd/ReadVariableOpReadVariableOpdown_level_2_no_0/bias*
dtype0*
_output_shapes	
:
б
down_level_2_no_0/BiasAddBiasAdddown_level_2_no_0/convolution(down_level_2_no_0/BiasAdd/ReadVariableOp*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
data_formatNHWC*
T0

down_level_2_no_0/ReluReludown_level_2_no_0/BiasAdd*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ

&down_level_2_no_1/random_uniform/shapeConst*
dtype0*%
valueB"            *
_output_shapes
:
i
$down_level_2_no_1/random_uniform/minConst*
valueB
 *ьQН*
_output_shapes
: *
dtype0
i
$down_level_2_no_1/random_uniform/maxConst*
valueB
 *ьQ=*
dtype0*
_output_shapes
: 
Ц
.down_level_2_no_1/random_uniform/RandomUniformRandomUniform&down_level_2_no_1/random_uniform/shape*(
_output_shapes
:*
seed2ПЉ*
T0*
seedБџх)*
dtype0

$down_level_2_no_1/random_uniform/subSub$down_level_2_no_1/random_uniform/max$down_level_2_no_1/random_uniform/min*
_output_shapes
: *
T0
Д
$down_level_2_no_1/random_uniform/mulMul.down_level_2_no_1/random_uniform/RandomUniform$down_level_2_no_1/random_uniform/sub*
T0*(
_output_shapes
:
І
 down_level_2_no_1/random_uniformAdd$down_level_2_no_1/random_uniform/mul$down_level_2_no_1/random_uniform/min*(
_output_shapes
:*
T0
д
down_level_2_no_1/kernelVarHandleOp*+
_class!
loc:@down_level_2_no_1/kernel*
	container *
dtype0*)
shared_namedown_level_2_no_1/kernel*
shape:*
_output_shapes
: 

9down_level_2_no_1/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_2_no_1/kernel*
_output_shapes
: 
|
down_level_2_no_1/kernel/AssignAssignVariableOpdown_level_2_no_1/kernel down_level_2_no_1/random_uniform*
dtype0

,down_level_2_no_1/kernel/Read/ReadVariableOpReadVariableOpdown_level_2_no_1/kernel*(
_output_shapes
:*
dtype0
f
down_level_2_no_1/ConstConst*
dtype0*
valueB*    *
_output_shapes	
:
С
down_level_2_no_1/biasVarHandleOp*
dtype0*
_output_shapes
: *'
shared_namedown_level_2_no_1/bias*
shape:*)
_class
loc:@down_level_2_no_1/bias*
	container 
}
7down_level_2_no_1/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpdown_level_2_no_1/bias*
_output_shapes
: 
o
down_level_2_no_1/bias/AssignAssignVariableOpdown_level_2_no_1/biasdown_level_2_no_1/Const*
dtype0
~
*down_level_2_no_1/bias/Read/ReadVariableOpReadVariableOpdown_level_2_no_1/bias*
dtype0*
_output_shapes	
:

,down_level_2_no_1/convolution/ReadVariableOpReadVariableOpdown_level_2_no_1/kernel*
dtype0*(
_output_shapes
:
С
down_level_2_no_1/convolutionConv2Ddown_level_2_no_0/Relu,down_level_2_no_1/convolution/ReadVariableOp*
	dilations
*
use_cudnn_on_gpu(*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
explicit_paddings
 *
paddingSAME*
strides
*
data_formatNHWC
|
(down_level_2_no_1/BiasAdd/ReadVariableOpReadVariableOpdown_level_2_no_1/bias*
dtype0*
_output_shapes	
:
б
down_level_2_no_1/BiasAddBiasAdddown_level_2_no_1/convolution(down_level_2_no_1/BiasAdd/ReadVariableOp*
data_formatNHWC*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ

down_level_2_no_1/ReluReludown_level_2_no_1/BiasAdd*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
T0
а
max_2/MaxPoolMaxPooldown_level_2_no_1/Relu*
data_formatNHWC*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
ksize
*
paddingVALID*
strides

v
middle_0/random_uniform/shapeConst*
dtype0*%
valueB"            *
_output_shapes
:
`
middle_0/random_uniform/minConst*
valueB
 *ЋЊ*Н*
_output_shapes
: *
dtype0
`
middle_0/random_uniform/maxConst*
_output_shapes
: *
dtype0*
valueB
 *ЋЊ*=
Д
%middle_0/random_uniform/RandomUniformRandomUniformmiddle_0/random_uniform/shape*
T0*
seedБџх)*
seed2лз*(
_output_shapes
:*
dtype0
}
middle_0/random_uniform/subSubmiddle_0/random_uniform/maxmiddle_0/random_uniform/min*
_output_shapes
: *
T0

middle_0/random_uniform/mulMul%middle_0/random_uniform/RandomUniformmiddle_0/random_uniform/sub*
T0*(
_output_shapes
:

middle_0/random_uniformAddmiddle_0/random_uniform/mulmiddle_0/random_uniform/min*(
_output_shapes
:*
T0
Й
middle_0/kernelVarHandleOp* 
shared_namemiddle_0/kernel*
dtype0*
	container *
shape:*
_output_shapes
: *"
_class
loc:@middle_0/kernel
o
0middle_0/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpmiddle_0/kernel*
_output_shapes
: 
a
middle_0/kernel/AssignAssignVariableOpmiddle_0/kernelmiddle_0/random_uniform*
dtype0
}
#middle_0/kernel/Read/ReadVariableOpReadVariableOpmiddle_0/kernel*(
_output_shapes
:*
dtype0
]
middle_0/ConstConst*
valueB*    *
_output_shapes	
:*
dtype0
І
middle_0/biasVarHandleOp*
dtype0*
shape:*
_output_shapes
: * 
_class
loc:@middle_0/bias*
shared_namemiddle_0/bias*
	container 
k
.middle_0/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpmiddle_0/bias*
_output_shapes
: 
T
middle_0/bias/AssignAssignVariableOpmiddle_0/biasmiddle_0/Const*
dtype0
l
!middle_0/bias/Read/ReadVariableOpReadVariableOpmiddle_0/bias*
_output_shapes	
:*
dtype0
}
#middle_0/convolution/ReadVariableOpReadVariableOpmiddle_0/kernel*(
_output_shapes
:*
dtype0
І
middle_0/convolutionConv2Dmax_2/MaxPool#middle_0/convolution/ReadVariableOp*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
data_formatNHWC*
paddingSAME*
use_cudnn_on_gpu(*
strides
*
T0*
explicit_paddings
 *
	dilations

j
middle_0/BiasAdd/ReadVariableOpReadVariableOpmiddle_0/bias*
_output_shapes	
:*
dtype0
Ж
middle_0/BiasAddBiasAddmiddle_0/convolutionmiddle_0/BiasAdd/ReadVariableOp*
T0*
data_formatNHWC*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ
t
middle_0/ReluRelumiddle_0/BiasAdd*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
T0
v
middle_2/random_uniform/shapeConst*%
valueB"            *
dtype0*
_output_shapes
:
`
middle_2/random_uniform/minConst*
valueB
 *ЋЊ*Н*
_output_shapes
: *
dtype0
`
middle_2/random_uniform/maxConst*
valueB
 *ЋЊ*=*
_output_shapes
: *
dtype0
Г
%middle_2/random_uniform/RandomUniformRandomUniformmiddle_2/random_uniform/shape*
seedБџх)*
T0*
seed2уаM*
dtype0*(
_output_shapes
:
}
middle_2/random_uniform/subSubmiddle_2/random_uniform/maxmiddle_2/random_uniform/min*
T0*
_output_shapes
: 

middle_2/random_uniform/mulMul%middle_2/random_uniform/RandomUniformmiddle_2/random_uniform/sub*
T0*(
_output_shapes
:

middle_2/random_uniformAddmiddle_2/random_uniform/mulmiddle_2/random_uniform/min*
T0*(
_output_shapes
:
Й
middle_2/kernelVarHandleOp*
_output_shapes
: *"
_class
loc:@middle_2/kernel*
	container *
shape:* 
shared_namemiddle_2/kernel*
dtype0
o
0middle_2/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpmiddle_2/kernel*
_output_shapes
: 
a
middle_2/kernel/AssignAssignVariableOpmiddle_2/kernelmiddle_2/random_uniform*
dtype0
}
#middle_2/kernel/Read/ReadVariableOpReadVariableOpmiddle_2/kernel*(
_output_shapes
:*
dtype0
]
middle_2/ConstConst*
_output_shapes	
:*
dtype0*
valueB*    
І
middle_2/biasVarHandleOp*
_output_shapes
: * 
_class
loc:@middle_2/bias*
shared_namemiddle_2/bias*
dtype0*
shape:*
	container 
k
.middle_2/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpmiddle_2/bias*
_output_shapes
: 
T
middle_2/bias/AssignAssignVariableOpmiddle_2/biasmiddle_2/Const*
dtype0
l
!middle_2/bias/Read/ReadVariableOpReadVariableOpmiddle_2/bias*
_output_shapes	
:*
dtype0
}
#middle_2/convolution/ReadVariableOpReadVariableOpmiddle_2/kernel*
dtype0*(
_output_shapes
:
І
middle_2/convolutionConv2Dmiddle_0/Relu#middle_2/convolution/ReadVariableOp*
paddingSAME*
data_formatNHWC*
use_cudnn_on_gpu(*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
explicit_paddings
 *
	dilations
*
strides

j
middle_2/BiasAdd/ReadVariableOpReadVariableOpmiddle_2/bias*
dtype0*
_output_shapes	
:
Ж
middle_2/BiasAddBiasAddmiddle_2/convolutionmiddle_2/BiasAdd/ReadVariableOp*
T0*
data_formatNHWC*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ
t
middle_2/ReluRelumiddle_2/BiasAdd*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ
b
up_sampling2d_1/ShapeShapemiddle_2/Relu*
_output_shapes
:*
out_type0*
T0
m
#up_sampling2d_1/strided_slice/stackConst*
valueB:*
_output_shapes
:*
dtype0
o
%up_sampling2d_1/strided_slice/stack_1Const*
_output_shapes
:*
dtype0*
valueB:
o
%up_sampling2d_1/strided_slice/stack_2Const*
valueB:*
dtype0*
_output_shapes
:
Э
up_sampling2d_1/strided_sliceStridedSliceup_sampling2d_1/Shape#up_sampling2d_1/strided_slice/stack%up_sampling2d_1/strided_slice/stack_1%up_sampling2d_1/strided_slice/stack_2*
end_mask *
T0*

begin_mask *
ellipsis_mask *
shrink_axis_mask *
Index0*
new_axis_mask *
_output_shapes
:
f
up_sampling2d_1/ConstConst*
_output_shapes
:*
dtype0*
valueB"      
u
up_sampling2d_1/mulMulup_sampling2d_1/strided_sliceup_sampling2d_1/Const*
_output_shapes
:*
T0
о
%up_sampling2d_1/ResizeNearestNeighborResizeNearestNeighbormiddle_2/Reluup_sampling2d_1/mul*
T0*
align_corners( *B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
half_pixel_centers( 
[
concatenate_1/concat/axisConst*
_output_shapes
: *
dtype0*
value	B :
м
concatenate_1/concatConcatV2%up_sampling2d_1/ResizeNearestNeighbordown_level_2_no_1/Reluconcatenate_1/concat/axis*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*

Tidx0*
N
}
$up_level_2_no_0/random_uniform/shapeConst*
dtype0*%
valueB"            *
_output_shapes
:
g
"up_level_2_no_0/random_uniform/minConst*
_output_shapes
: *
dtype0*
valueB
 *ЋЊ*Н
g
"up_level_2_no_0/random_uniform/maxConst*
dtype0*
_output_shapes
: *
valueB
 *ЋЊ*=
Т
,up_level_2_no_0/random_uniform/RandomUniformRandomUniform$up_level_2_no_0/random_uniform/shape*
seedБџх)*(
_output_shapes
:*
dtype0*
T0*
seed2Њк

"up_level_2_no_0/random_uniform/subSub"up_level_2_no_0/random_uniform/max"up_level_2_no_0/random_uniform/min*
_output_shapes
: *
T0
Ў
"up_level_2_no_0/random_uniform/mulMul,up_level_2_no_0/random_uniform/RandomUniform"up_level_2_no_0/random_uniform/sub*
T0*(
_output_shapes
:
 
up_level_2_no_0/random_uniformAdd"up_level_2_no_0/random_uniform/mul"up_level_2_no_0/random_uniform/min*(
_output_shapes
:*
T0
Ю
up_level_2_no_0/kernelVarHandleOp*
dtype0*
_output_shapes
: *
shape:*)
_class
loc:@up_level_2_no_0/kernel*
	container *'
shared_nameup_level_2_no_0/kernel
}
7up_level_2_no_0/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_2_no_0/kernel*
_output_shapes
: 
v
up_level_2_no_0/kernel/AssignAssignVariableOpup_level_2_no_0/kernelup_level_2_no_0/random_uniform*
dtype0

*up_level_2_no_0/kernel/Read/ReadVariableOpReadVariableOpup_level_2_no_0/kernel*(
_output_shapes
:*
dtype0
d
up_level_2_no_0/ConstConst*
dtype0*
_output_shapes	
:*
valueB*    
Л
up_level_2_no_0/biasVarHandleOp*
	container *
shape:*'
_class
loc:@up_level_2_no_0/bias*%
shared_nameup_level_2_no_0/bias*
_output_shapes
: *
dtype0
y
5up_level_2_no_0/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_2_no_0/bias*
_output_shapes
: 
i
up_level_2_no_0/bias/AssignAssignVariableOpup_level_2_no_0/biasup_level_2_no_0/Const*
dtype0
z
(up_level_2_no_0/bias/Read/ReadVariableOpReadVariableOpup_level_2_no_0/bias*
_output_shapes	
:*
dtype0

*up_level_2_no_0/convolution/ReadVariableOpReadVariableOpup_level_2_no_0/kernel*
dtype0*(
_output_shapes
:
Л
up_level_2_no_0/convolutionConv2Dconcatenate_1/concat*up_level_2_no_0/convolution/ReadVariableOp*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
data_formatNHWC*
strides
*
use_cudnn_on_gpu(*
explicit_paddings
 *
	dilations
*
T0*
paddingSAME
x
&up_level_2_no_0/BiasAdd/ReadVariableOpReadVariableOpup_level_2_no_0/bias*
dtype0*
_output_shapes	
:
Ы
up_level_2_no_0/BiasAddBiasAddup_level_2_no_0/convolution&up_level_2_no_0/BiasAdd/ReadVariableOp*
T0*
data_formatNHWC*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ

up_level_2_no_0/ReluReluup_level_2_no_0/BiasAdd*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ
}
$up_level_2_no_2/random_uniform/shapeConst*
_output_shapes
:*
dtype0*%
valueB"         @   
g
"up_level_2_no_2/random_uniform/minConst*
_output_shapes
: *
dtype0*
valueB
 *я[qН
g
"up_level_2_no_2/random_uniform/maxConst*
_output_shapes
: *
valueB
 *я[q=*
dtype0
С
,up_level_2_no_2/random_uniform/RandomUniformRandomUniform$up_level_2_no_2/random_uniform/shape*
seedБџх)*
T0*
dtype0*
seed2Гё*'
_output_shapes
:@

"up_level_2_no_2/random_uniform/subSub"up_level_2_no_2/random_uniform/max"up_level_2_no_2/random_uniform/min*
T0*
_output_shapes
: 
­
"up_level_2_no_2/random_uniform/mulMul,up_level_2_no_2/random_uniform/RandomUniform"up_level_2_no_2/random_uniform/sub*
T0*'
_output_shapes
:@

up_level_2_no_2/random_uniformAdd"up_level_2_no_2/random_uniform/mul"up_level_2_no_2/random_uniform/min*'
_output_shapes
:@*
T0
Э
up_level_2_no_2/kernelVarHandleOp*
shape:@*'
shared_nameup_level_2_no_2/kernel*
_output_shapes
: *
dtype0*)
_class
loc:@up_level_2_no_2/kernel*
	container 
}
7up_level_2_no_2/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_2_no_2/kernel*
_output_shapes
: 
v
up_level_2_no_2/kernel/AssignAssignVariableOpup_level_2_no_2/kernelup_level_2_no_2/random_uniform*
dtype0

*up_level_2_no_2/kernel/Read/ReadVariableOpReadVariableOpup_level_2_no_2/kernel*
dtype0*'
_output_shapes
:@
b
up_level_2_no_2/ConstConst*
_output_shapes
:@*
valueB@*    *
dtype0
К
up_level_2_no_2/biasVarHandleOp*
	container *
shape:@*%
shared_nameup_level_2_no_2/bias*
_output_shapes
: *
dtype0*'
_class
loc:@up_level_2_no_2/bias
y
5up_level_2_no_2/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_2_no_2/bias*
_output_shapes
: 
i
up_level_2_no_2/bias/AssignAssignVariableOpup_level_2_no_2/biasup_level_2_no_2/Const*
dtype0
y
(up_level_2_no_2/bias/Read/ReadVariableOpReadVariableOpup_level_2_no_2/bias*
_output_shapes
:@*
dtype0

*up_level_2_no_2/convolution/ReadVariableOpReadVariableOpup_level_2_no_2/kernel*
dtype0*'
_output_shapes
:@
К
up_level_2_no_2/convolutionConv2Dup_level_2_no_0/Relu*up_level_2_no_2/convolution/ReadVariableOp*
T0*
	dilations
*
use_cudnn_on_gpu(*
explicit_paddings
 *
paddingSAME*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
data_formatNHWC*
strides

w
&up_level_2_no_2/BiasAdd/ReadVariableOpReadVariableOpup_level_2_no_2/bias*
_output_shapes
:@*
dtype0
Ъ
up_level_2_no_2/BiasAddBiasAddup_level_2_no_2/convolution&up_level_2_no_2/BiasAdd/ReadVariableOp*
data_formatNHWC*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@

up_level_2_no_2/ReluReluup_level_2_no_2/BiasAdd*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
T0
i
up_sampling2d_2/ShapeShapeup_level_2_no_2/Relu*
out_type0*
T0*
_output_shapes
:
m
#up_sampling2d_2/strided_slice/stackConst*
_output_shapes
:*
valueB:*
dtype0
o
%up_sampling2d_2/strided_slice/stack_1Const*
dtype0*
_output_shapes
:*
valueB:
o
%up_sampling2d_2/strided_slice/stack_2Const*
_output_shapes
:*
dtype0*
valueB:
Э
up_sampling2d_2/strided_sliceStridedSliceup_sampling2d_2/Shape#up_sampling2d_2/strided_slice/stack%up_sampling2d_2/strided_slice/stack_1%up_sampling2d_2/strided_slice/stack_2*

begin_mask *
ellipsis_mask *
shrink_axis_mask *
Index0*
end_mask *
_output_shapes
:*
T0*
new_axis_mask 
f
up_sampling2d_2/ConstConst*
_output_shapes
:*
valueB"      *
dtype0
u
up_sampling2d_2/mulMulup_sampling2d_2/strided_sliceup_sampling2d_2/Const*
_output_shapes
:*
T0
ф
%up_sampling2d_2/ResizeNearestNeighborResizeNearestNeighborup_level_2_no_2/Reluup_sampling2d_2/mul*
half_pixel_centers( *
align_corners( *
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@
[
concatenate_2/concat/axisConst*
_output_shapes
: *
dtype0*
value	B :
м
concatenate_2/concatConcatV2%up_sampling2d_2/ResizeNearestNeighbordown_level_1_no_1/Reluconcatenate_2/concat/axis*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*

Tidx0*
N
}
$up_level_1_no_0/random_uniform/shapeConst*
_output_shapes
:*%
valueB"         @   *
dtype0
g
"up_level_1_no_0/random_uniform/minConst*
valueB
 *я[qН*
_output_shapes
: *
dtype0
g
"up_level_1_no_0/random_uniform/maxConst*
_output_shapes
: *
dtype0*
valueB
 *я[q=
С
,up_level_1_no_0/random_uniform/RandomUniformRandomUniform$up_level_1_no_0/random_uniform/shape*
seedБџх)*
seed2кЮД*'
_output_shapes
:@*
T0*
dtype0

"up_level_1_no_0/random_uniform/subSub"up_level_1_no_0/random_uniform/max"up_level_1_no_0/random_uniform/min*
T0*
_output_shapes
: 
­
"up_level_1_no_0/random_uniform/mulMul,up_level_1_no_0/random_uniform/RandomUniform"up_level_1_no_0/random_uniform/sub*'
_output_shapes
:@*
T0

up_level_1_no_0/random_uniformAdd"up_level_1_no_0/random_uniform/mul"up_level_1_no_0/random_uniform/min*
T0*'
_output_shapes
:@
Э
up_level_1_no_0/kernelVarHandleOp*'
shared_nameup_level_1_no_0/kernel*
dtype0*
_output_shapes
: *
	container *
shape:@*)
_class
loc:@up_level_1_no_0/kernel
}
7up_level_1_no_0/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_1_no_0/kernel*
_output_shapes
: 
v
up_level_1_no_0/kernel/AssignAssignVariableOpup_level_1_no_0/kernelup_level_1_no_0/random_uniform*
dtype0

*up_level_1_no_0/kernel/Read/ReadVariableOpReadVariableOpup_level_1_no_0/kernel*'
_output_shapes
:@*
dtype0
b
up_level_1_no_0/ConstConst*
dtype0*
_output_shapes
:@*
valueB@*    
К
up_level_1_no_0/biasVarHandleOp*%
shared_nameup_level_1_no_0/bias*
_output_shapes
: *
dtype0*
	container *
shape:@*'
_class
loc:@up_level_1_no_0/bias
y
5up_level_1_no_0/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_1_no_0/bias*
_output_shapes
: 
i
up_level_1_no_0/bias/AssignAssignVariableOpup_level_1_no_0/biasup_level_1_no_0/Const*
dtype0
y
(up_level_1_no_0/bias/Read/ReadVariableOpReadVariableOpup_level_1_no_0/bias*
_output_shapes
:@*
dtype0

*up_level_1_no_0/convolution/ReadVariableOpReadVariableOpup_level_1_no_0/kernel*
dtype0*'
_output_shapes
:@
К
up_level_1_no_0/convolutionConv2Dconcatenate_2/concat*up_level_1_no_0/convolution/ReadVariableOp*
use_cudnn_on_gpu(*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
T0*
strides
*
data_formatNHWC*
explicit_paddings
 *
	dilations
*
paddingSAME
w
&up_level_1_no_0/BiasAdd/ReadVariableOpReadVariableOpup_level_1_no_0/bias*
_output_shapes
:@*
dtype0
Ъ
up_level_1_no_0/BiasAddBiasAddup_level_1_no_0/convolution&up_level_1_no_0/BiasAdd/ReadVariableOp*
data_formatNHWC*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
T0

up_level_1_no_0/ReluReluup_level_1_no_0/BiasAdd*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
T0
}
$up_level_1_no_2/random_uniform/shapeConst*%
valueB"      @       *
dtype0*
_output_shapes
:
g
"up_level_1_no_2/random_uniform/minConst*
_output_shapes
: *
dtype0*
valueB
 *ЋЊЊН
g
"up_level_1_no_2/random_uniform/maxConst*
dtype0*
valueB
 *ЋЊЊ=*
_output_shapes
: 
Р
,up_level_1_no_2/random_uniform/RandomUniformRandomUniform$up_level_1_no_2/random_uniform/shape*
seed2ЎЩФ*&
_output_shapes
:@ *
dtype0*
T0*
seedБџх)

"up_level_1_no_2/random_uniform/subSub"up_level_1_no_2/random_uniform/max"up_level_1_no_2/random_uniform/min*
_output_shapes
: *
T0
Ќ
"up_level_1_no_2/random_uniform/mulMul,up_level_1_no_2/random_uniform/RandomUniform"up_level_1_no_2/random_uniform/sub*
T0*&
_output_shapes
:@ 

up_level_1_no_2/random_uniformAdd"up_level_1_no_2/random_uniform/mul"up_level_1_no_2/random_uniform/min*
T0*&
_output_shapes
:@ 
Ь
up_level_1_no_2/kernelVarHandleOp*
dtype0*
shape:@ *)
_class
loc:@up_level_1_no_2/kernel*'
shared_nameup_level_1_no_2/kernel*
_output_shapes
: *
	container 
}
7up_level_1_no_2/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_1_no_2/kernel*
_output_shapes
: 
v
up_level_1_no_2/kernel/AssignAssignVariableOpup_level_1_no_2/kernelup_level_1_no_2/random_uniform*
dtype0

*up_level_1_no_2/kernel/Read/ReadVariableOpReadVariableOpup_level_1_no_2/kernel*&
_output_shapes
:@ *
dtype0
b
up_level_1_no_2/ConstConst*
_output_shapes
: *
valueB *    *
dtype0
К
up_level_1_no_2/biasVarHandleOp*
shape: *
dtype0*%
shared_nameup_level_1_no_2/bias*
_output_shapes
: *
	container *'
_class
loc:@up_level_1_no_2/bias
y
5up_level_1_no_2/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_1_no_2/bias*
_output_shapes
: 
i
up_level_1_no_2/bias/AssignAssignVariableOpup_level_1_no_2/biasup_level_1_no_2/Const*
dtype0
y
(up_level_1_no_2/bias/Read/ReadVariableOpReadVariableOpup_level_1_no_2/bias*
_output_shapes
: *
dtype0

*up_level_1_no_2/convolution/ReadVariableOpReadVariableOpup_level_1_no_2/kernel*&
_output_shapes
:@ *
dtype0
К
up_level_1_no_2/convolutionConv2Dup_level_1_no_0/Relu*up_level_1_no_2/convolution/ReadVariableOp*
	dilations
*
use_cudnn_on_gpu(*
T0*
strides
*
explicit_paddings
 *
paddingSAME*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
data_formatNHWC
w
&up_level_1_no_2/BiasAdd/ReadVariableOpReadVariableOpup_level_1_no_2/bias*
dtype0*
_output_shapes
: 
Ъ
up_level_1_no_2/BiasAddBiasAddup_level_1_no_2/convolution&up_level_1_no_2/BiasAdd/ReadVariableOp*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
data_formatNHWC*
T0

up_level_1_no_2/ReluReluup_level_1_no_2/BiasAdd*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 
i
up_sampling2d_3/ShapeShapeup_level_1_no_2/Relu*
T0*
_output_shapes
:*
out_type0
m
#up_sampling2d_3/strided_slice/stackConst*
valueB:*
dtype0*
_output_shapes
:
o
%up_sampling2d_3/strided_slice/stack_1Const*
dtype0*
valueB:*
_output_shapes
:
o
%up_sampling2d_3/strided_slice/stack_2Const*
dtype0*
valueB:*
_output_shapes
:
Э
up_sampling2d_3/strided_sliceStridedSliceup_sampling2d_3/Shape#up_sampling2d_3/strided_slice/stack%up_sampling2d_3/strided_slice/stack_1%up_sampling2d_3/strided_slice/stack_2*
ellipsis_mask *
end_mask *
_output_shapes
:*
T0*
Index0*
shrink_axis_mask *
new_axis_mask *

begin_mask 
f
up_sampling2d_3/ConstConst*
dtype0*
valueB"      *
_output_shapes
:
u
up_sampling2d_3/mulMulup_sampling2d_3/strided_sliceup_sampling2d_3/Const*
T0*
_output_shapes
:
ф
%up_sampling2d_3/ResizeNearestNeighborResizeNearestNeighborup_level_1_no_2/Reluup_sampling2d_3/mul*
align_corners( *
T0*
half_pixel_centers( *A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 
[
concatenate_3/concat/axisConst*
_output_shapes
: *
dtype0*
value	B :
л
concatenate_3/concatConcatV2%up_sampling2d_3/ResizeNearestNeighbordown_level_0_no_1/Reluconcatenate_3/concat/axis*

Tidx0*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ@*
N
}
$up_level_0_no_0/random_uniform/shapeConst*
_output_shapes
:*
dtype0*%
valueB"      @       
g
"up_level_0_no_0/random_uniform/minConst*
_output_shapes
: *
valueB
 *ЋЊЊН*
dtype0
g
"up_level_0_no_0/random_uniform/maxConst*
_output_shapes
: *
dtype0*
valueB
 *ЋЊЊ=
Р
,up_level_0_no_0/random_uniform/RandomUniformRandomUniform$up_level_0_no_0/random_uniform/shape*
dtype0*
T0*
seedБџх)*&
_output_shapes
:@ *
seed2УУЄ

"up_level_0_no_0/random_uniform/subSub"up_level_0_no_0/random_uniform/max"up_level_0_no_0/random_uniform/min*
_output_shapes
: *
T0
Ќ
"up_level_0_no_0/random_uniform/mulMul,up_level_0_no_0/random_uniform/RandomUniform"up_level_0_no_0/random_uniform/sub*
T0*&
_output_shapes
:@ 

up_level_0_no_0/random_uniformAdd"up_level_0_no_0/random_uniform/mul"up_level_0_no_0/random_uniform/min*&
_output_shapes
:@ *
T0
Ь
up_level_0_no_0/kernelVarHandleOp*
_output_shapes
: *)
_class
loc:@up_level_0_no_0/kernel*
	container *
shape:@ *
dtype0*'
shared_nameup_level_0_no_0/kernel
}
7up_level_0_no_0/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_0_no_0/kernel*
_output_shapes
: 
v
up_level_0_no_0/kernel/AssignAssignVariableOpup_level_0_no_0/kernelup_level_0_no_0/random_uniform*
dtype0

*up_level_0_no_0/kernel/Read/ReadVariableOpReadVariableOpup_level_0_no_0/kernel*&
_output_shapes
:@ *
dtype0
b
up_level_0_no_0/ConstConst*
_output_shapes
: *
dtype0*
valueB *    
К
up_level_0_no_0/biasVarHandleOp*%
shared_nameup_level_0_no_0/bias*'
_class
loc:@up_level_0_no_0/bias*
_output_shapes
: *
dtype0*
	container *
shape: 
y
5up_level_0_no_0/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_0_no_0/bias*
_output_shapes
: 
i
up_level_0_no_0/bias/AssignAssignVariableOpup_level_0_no_0/biasup_level_0_no_0/Const*
dtype0
y
(up_level_0_no_0/bias/Read/ReadVariableOpReadVariableOpup_level_0_no_0/bias*
dtype0*
_output_shapes
: 

*up_level_0_no_0/convolution/ReadVariableOpReadVariableOpup_level_0_no_0/kernel*&
_output_shapes
:@ *
dtype0
К
up_level_0_no_0/convolutionConv2Dconcatenate_3/concat*up_level_0_no_0/convolution/ReadVariableOp*
T0*
	dilations
*
use_cudnn_on_gpu(*
strides
*
explicit_paddings
 *
paddingSAME*
data_formatNHWC*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 
w
&up_level_0_no_0/BiasAdd/ReadVariableOpReadVariableOpup_level_0_no_0/bias*
dtype0*
_output_shapes
: 
Ъ
up_level_0_no_0/BiasAddBiasAddup_level_0_no_0/convolution&up_level_0_no_0/BiasAdd/ReadVariableOp*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
T0*
data_formatNHWC

up_level_0_no_0/ReluReluup_level_0_no_0/BiasAdd*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
T0
}
$up_level_0_no_2/random_uniform/shapeConst*
_output_shapes
:*%
valueB"              *
dtype0
g
"up_level_0_no_2/random_uniform/minConst*
dtype0*
_output_shapes
: *
valueB
 *ьбН
g
"up_level_0_no_2/random_uniform/maxConst*
_output_shapes
: *
valueB
 *ьб=*
dtype0
Р
,up_level_0_no_2/random_uniform/RandomUniformRandomUniform$up_level_0_no_2/random_uniform/shape*
dtype0*
T0*
seedБџх)*&
_output_shapes
:  *
seed2

"up_level_0_no_2/random_uniform/subSub"up_level_0_no_2/random_uniform/max"up_level_0_no_2/random_uniform/min*
_output_shapes
: *
T0
Ќ
"up_level_0_no_2/random_uniform/mulMul,up_level_0_no_2/random_uniform/RandomUniform"up_level_0_no_2/random_uniform/sub*&
_output_shapes
:  *
T0

up_level_0_no_2/random_uniformAdd"up_level_0_no_2/random_uniform/mul"up_level_0_no_2/random_uniform/min*
T0*&
_output_shapes
:  
Ь
up_level_0_no_2/kernelVarHandleOp*
dtype0*
_output_shapes
: *'
shared_nameup_level_0_no_2/kernel*)
_class
loc:@up_level_0_no_2/kernel*
	container *
shape:  
}
7up_level_0_no_2/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_0_no_2/kernel*
_output_shapes
: 
v
up_level_0_no_2/kernel/AssignAssignVariableOpup_level_0_no_2/kernelup_level_0_no_2/random_uniform*
dtype0

*up_level_0_no_2/kernel/Read/ReadVariableOpReadVariableOpup_level_0_no_2/kernel*&
_output_shapes
:  *
dtype0
b
up_level_0_no_2/ConstConst*
dtype0*
valueB *    *
_output_shapes
: 
К
up_level_0_no_2/biasVarHandleOp*
_output_shapes
: *
dtype0*%
shared_nameup_level_0_no_2/bias*'
_class
loc:@up_level_0_no_2/bias*
shape: *
	container 
y
5up_level_0_no_2/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpup_level_0_no_2/bias*
_output_shapes
: 
i
up_level_0_no_2/bias/AssignAssignVariableOpup_level_0_no_2/biasup_level_0_no_2/Const*
dtype0
y
(up_level_0_no_2/bias/Read/ReadVariableOpReadVariableOpup_level_0_no_2/bias*
_output_shapes
: *
dtype0

*up_level_0_no_2/convolution/ReadVariableOpReadVariableOpup_level_0_no_2/kernel*&
_output_shapes
:  *
dtype0
К
up_level_0_no_2/convolutionConv2Dup_level_0_no_0/Relu*up_level_0_no_2/convolution/ReadVariableOp*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
data_formatNHWC*
strides
*
	dilations
*
use_cudnn_on_gpu(*
paddingSAME*
T0*
explicit_paddings
 
w
&up_level_0_no_2/BiasAdd/ReadVariableOpReadVariableOpup_level_0_no_2/bias*
dtype0*
_output_shapes
: 
Ъ
up_level_0_no_2/BiasAddBiasAddup_level_0_no_2/convolution&up_level_0_no_2/BiasAdd/ReadVariableOp*
T0*
data_formatNHWC*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 

up_level_0_no_2/ReluReluup_level_0_no_2/BiasAdd*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
T0
v
features/random_uniform/shapeConst*
_output_shapes
:*%
valueB"             *
dtype0
`
features/random_uniform/minConst*
dtype0*
valueB
 *Ѕ2Н*
_output_shapes
: 
`
features/random_uniform/maxConst*
_output_shapes
: *
valueB
 *Ѕ2=*
dtype0
Г
%features/random_uniform/RandomUniformRandomUniformfeatures/random_uniform/shape*
T0*
seedБџх)*
seed2ц џ*
dtype0*'
_output_shapes
: 
}
features/random_uniform/subSubfeatures/random_uniform/maxfeatures/random_uniform/min*
T0*
_output_shapes
: 

features/random_uniform/mulMul%features/random_uniform/RandomUniformfeatures/random_uniform/sub*'
_output_shapes
: *
T0

features/random_uniformAddfeatures/random_uniform/mulfeatures/random_uniform/min*
T0*'
_output_shapes
: 
И
features/kernelVarHandleOp*"
_class
loc:@features/kernel*
dtype0*
_output_shapes
: *
shape: * 
shared_namefeatures/kernel*
	container 
o
0features/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpfeatures/kernel*
_output_shapes
: 
a
features/kernel/AssignAssignVariableOpfeatures/kernelfeatures/random_uniform*
dtype0
|
#features/kernel/Read/ReadVariableOpReadVariableOpfeatures/kernel*
dtype0*'
_output_shapes
: 
]
features/ConstConst*
_output_shapes	
:*
dtype0*
valueB*    
І
features/biasVarHandleOp*
	container *
shape:*
_output_shapes
: *
dtype0* 
_class
loc:@features/bias*
shared_namefeatures/bias
k
.features/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOpfeatures/bias*
_output_shapes
: 
T
features/bias/AssignAssignVariableOpfeatures/biasfeatures/Const*
dtype0
l
!features/bias/Read/ReadVariableOpReadVariableOpfeatures/bias*
dtype0*
_output_shapes	
:
|
#features/convolution/ReadVariableOpReadVariableOpfeatures/kernel*'
_output_shapes
: *
dtype0
­
features/convolutionConv2Dup_level_0_no_2/Relu#features/convolution/ReadVariableOp*
use_cudnn_on_gpu(*
explicit_paddings
 *
	dilations
*
strides
*
data_formatNHWC*
T0*
paddingSAME*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ
j
features/BiasAdd/ReadVariableOpReadVariableOpfeatures/bias*
_output_shapes	
:*
dtype0
Ж
features/BiasAddBiasAddfeatures/convolutionfeatures/BiasAdd/ReadVariableOp*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
data_formatNHWC
t
features/ReluRelufeatures/BiasAdd*
T0*B
_output_shapes0
.:,џџџџџџџџџџџџџџџџџџџџџџџџџџџ
r
prob/random_uniform/shapeConst*%
valueB"            *
dtype0*
_output_shapes
:
\
prob/random_uniform/minConst*
_output_shapes
: *
dtype0*
valueB
 *nз\О
\
prob/random_uniform/maxConst*
_output_shapes
: *
dtype0*
valueB
 *nз\>
Ћ
!prob/random_uniform/RandomUniformRandomUniformprob/random_uniform/shape*
seedБџх)*
dtype0*
seed2Г *
T0*'
_output_shapes
:
q
prob/random_uniform/subSubprob/random_uniform/maxprob/random_uniform/min*
T0*
_output_shapes
: 

prob/random_uniform/mulMul!prob/random_uniform/RandomUniformprob/random_uniform/sub*
T0*'
_output_shapes
:
~
prob/random_uniformAddprob/random_uniform/mulprob/random_uniform/min*
T0*'
_output_shapes
:
Ќ
prob/kernelVarHandleOp*
shape:*
_output_shapes
: *
	container *
_class
loc:@prob/kernel*
shared_nameprob/kernel*
dtype0
g
,prob/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpprob/kernel*
_output_shapes
: 
U
prob/kernel/AssignAssignVariableOpprob/kernelprob/random_uniform*
dtype0
t
prob/kernel/Read/ReadVariableOpReadVariableOpprob/kernel*'
_output_shapes
:*
dtype0
W

prob/ConstConst*
valueB*    *
_output_shapes
:*
dtype0

	prob/biasVarHandleOp*
_output_shapes
: *
shape:*
_class
loc:@prob/bias*
shared_name	prob/bias*
dtype0*
	container 
c
*prob/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOp	prob/bias*
_output_shapes
: 
H
prob/bias/AssignAssignVariableOp	prob/bias
prob/Const*
dtype0
c
prob/bias/Read/ReadVariableOpReadVariableOp	prob/bias*
dtype0*
_output_shapes
:
t
prob/convolution/ReadVariableOpReadVariableOpprob/kernel*'
_output_shapes
:*
dtype0

prob/convolutionConv2Dfeatures/Reluprob/convolution/ReadVariableOp*
	dilations
*
data_formatNHWC*
strides
*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
T0*
use_cudnn_on_gpu(*
explicit_paddings
 *
paddingSAME
a
prob/BiasAdd/ReadVariableOpReadVariableOp	prob/bias*
_output_shapes
:*
dtype0
Љ
prob/BiasAddBiasAddprob/convolutionprob/BiasAdd/ReadVariableOp*
data_formatNHWC*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ
q
prob/SigmoidSigmoidprob/BiasAdd*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
T0
r
dist/random_uniform/shapeConst*%
valueB"             *
dtype0*
_output_shapes
:
\
dist/random_uniform/minConst*
_output_shapes
: *
valueB
 *јKFО*
dtype0
\
dist/random_uniform/maxConst*
_output_shapes
: *
valueB
 *јKF>*
dtype0
Њ
!dist/random_uniform/RandomUniformRandomUniformdist/random_uniform/shape*
seed2­Ё,*'
_output_shapes
: *
dtype0*
seedБџх)*
T0
q
dist/random_uniform/subSubdist/random_uniform/maxdist/random_uniform/min*
_output_shapes
: *
T0

dist/random_uniform/mulMul!dist/random_uniform/RandomUniformdist/random_uniform/sub*'
_output_shapes
: *
T0
~
dist/random_uniformAdddist/random_uniform/muldist/random_uniform/min*'
_output_shapes
: *
T0
Ќ
dist/kernelVarHandleOp*
shared_namedist/kernel*
_class
loc:@dist/kernel*
dtype0*
	container *
_output_shapes
: *
shape: 
g
,dist/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpdist/kernel*
_output_shapes
: 
U
dist/kernel/AssignAssignVariableOpdist/kerneldist/random_uniform*
dtype0
t
dist/kernel/Read/ReadVariableOpReadVariableOpdist/kernel*
dtype0*'
_output_shapes
: 
W

dist/ConstConst*
_output_shapes
: *
dtype0*
valueB *    

	dist/biasVarHandleOp*
dtype0*
	container *
_output_shapes
: *
shape: *
shared_name	dist/bias*
_class
loc:@dist/bias
c
*dist/bias/IsInitialized/VarIsInitializedOpVarIsInitializedOp	dist/bias*
_output_shapes
: 
H
dist/bias/AssignAssignVariableOp	dist/bias
dist/Const*
dtype0
c
dist/bias/Read/ReadVariableOpReadVariableOp	dist/bias*
_output_shapes
: *
dtype0
t
dist/convolution/ReadVariableOpReadVariableOpdist/kernel*
dtype0*'
_output_shapes
: 

dist/convolutionConv2Dfeatures/Reludist/convolution/ReadVariableOp*
explicit_paddings
 *
strides
*
data_formatNHWC*
	dilations
*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
paddingSAME*
use_cudnn_on_gpu(*
T0
a
dist/BiasAdd/ReadVariableOpReadVariableOp	dist/bias*
dtype0*
_output_shapes
: 
Љ
dist/BiasAddBiasAdddist/convolutiondist/BiasAdd/ReadVariableOp*
data_formatNHWC*
T0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ 
Д
PlaceholderPlaceholder*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0
O
AssignVariableOpAssignVariableOpconv2d_1/kernelPlaceholder*
dtype0
y
ReadVariableOpReadVariableOpconv2d_1/kernel^AssignVariableOp*
dtype0*&
_output_shapes
: 
h
Placeholder_1Placeholder*
dtype0*
shape:џџџџџџџџџ*#
_output_shapes
:џџџџџџџџџ
Q
AssignVariableOp_1AssignVariableOpconv2d_1/biasPlaceholder_1*
dtype0
o
ReadVariableOp_1ReadVariableOpconv2d_1/bias^AssignVariableOp_1*
dtype0*
_output_shapes
: 
Ж
Placeholder_2Placeholder*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
S
AssignVariableOp_2AssignVariableOpconv2d_2/kernelPlaceholder_2*
dtype0
}
ReadVariableOp_2ReadVariableOpconv2d_2/kernel^AssignVariableOp_2*&
_output_shapes
:  *
dtype0
h
Placeholder_3Placeholder*#
_output_shapes
:џџџџџџџџџ*
shape:џџџџџџџџџ*
dtype0
Q
AssignVariableOp_3AssignVariableOpconv2d_2/biasPlaceholder_3*
dtype0
o
ReadVariableOp_3ReadVariableOpconv2d_2/bias^AssignVariableOp_3*
_output_shapes
: *
dtype0
Ж
Placeholder_4Placeholder*
dtype0*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
\
AssignVariableOp_4AssignVariableOpdown_level_0_no_0/kernelPlaceholder_4*
dtype0

ReadVariableOp_4ReadVariableOpdown_level_0_no_0/kernel^AssignVariableOp_4*&
_output_shapes
:  *
dtype0
h
Placeholder_5Placeholder*
shape:џџџџџџџџџ*
dtype0*#
_output_shapes
:џџџџџџџџџ
Z
AssignVariableOp_5AssignVariableOpdown_level_0_no_0/biasPlaceholder_5*
dtype0
x
ReadVariableOp_5ReadVariableOpdown_level_0_no_0/bias^AssignVariableOp_5*
_output_shapes
: *
dtype0
Ж
Placeholder_6Placeholder*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
\
AssignVariableOp_6AssignVariableOpdown_level_0_no_1/kernelPlaceholder_6*
dtype0

ReadVariableOp_6ReadVariableOpdown_level_0_no_1/kernel^AssignVariableOp_6*&
_output_shapes
:  *
dtype0
h
Placeholder_7Placeholder*
shape:џџџџџџџџџ*#
_output_shapes
:џџџџџџџџџ*
dtype0
Z
AssignVariableOp_7AssignVariableOpdown_level_0_no_1/biasPlaceholder_7*
dtype0
x
ReadVariableOp_7ReadVariableOpdown_level_0_no_1/bias^AssignVariableOp_7*
_output_shapes
: *
dtype0
Ж
Placeholder_8Placeholder*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
\
AssignVariableOp_8AssignVariableOpdown_level_1_no_0/kernelPlaceholder_8*
dtype0

ReadVariableOp_8ReadVariableOpdown_level_1_no_0/kernel^AssignVariableOp_8*&
_output_shapes
: @*
dtype0
h
Placeholder_9Placeholder*
shape:џџџџџџџџџ*
dtype0*#
_output_shapes
:џџџџџџџџџ
Z
AssignVariableOp_9AssignVariableOpdown_level_1_no_0/biasPlaceholder_9*
dtype0
x
ReadVariableOp_9ReadVariableOpdown_level_1_no_0/bias^AssignVariableOp_9*
_output_shapes
:@*
dtype0
З
Placeholder_10Placeholder*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
^
AssignVariableOp_10AssignVariableOpdown_level_1_no_1/kernelPlaceholder_10*
dtype0

ReadVariableOp_10ReadVariableOpdown_level_1_no_1/kernel^AssignVariableOp_10*
dtype0*&
_output_shapes
:@@
i
Placeholder_11Placeholder*#
_output_shapes
:џџџџџџџџџ*
shape:џџџџџџџџџ*
dtype0
\
AssignVariableOp_11AssignVariableOpdown_level_1_no_1/biasPlaceholder_11*
dtype0
z
ReadVariableOp_11ReadVariableOpdown_level_1_no_1/bias^AssignVariableOp_11*
_output_shapes
:@*
dtype0
З
Placeholder_12Placeholder*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
^
AssignVariableOp_12AssignVariableOpdown_level_2_no_0/kernelPlaceholder_12*
dtype0

ReadVariableOp_12ReadVariableOpdown_level_2_no_0/kernel^AssignVariableOp_12*
dtype0*'
_output_shapes
:@
i
Placeholder_13Placeholder*
shape:џџџџџџџџџ*
dtype0*#
_output_shapes
:џџџџџџџџџ
\
AssignVariableOp_13AssignVariableOpdown_level_2_no_0/biasPlaceholder_13*
dtype0
{
ReadVariableOp_13ReadVariableOpdown_level_2_no_0/bias^AssignVariableOp_13*
_output_shapes	
:*
dtype0
З
Placeholder_14Placeholder*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0
^
AssignVariableOp_14AssignVariableOpdown_level_2_no_1/kernelPlaceholder_14*
dtype0

ReadVariableOp_14ReadVariableOpdown_level_2_no_1/kernel^AssignVariableOp_14*(
_output_shapes
:*
dtype0
i
Placeholder_15Placeholder*
dtype0*
shape:џџџџџџџџџ*#
_output_shapes
:џџџџџџџџџ
\
AssignVariableOp_15AssignVariableOpdown_level_2_no_1/biasPlaceholder_15*
dtype0
{
ReadVariableOp_15ReadVariableOpdown_level_2_no_1/bias^AssignVariableOp_15*
_output_shapes	
:*
dtype0
З
Placeholder_16Placeholder*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
U
AssignVariableOp_16AssignVariableOpmiddle_0/kernelPlaceholder_16*
dtype0

ReadVariableOp_16ReadVariableOpmiddle_0/kernel^AssignVariableOp_16*
dtype0*(
_output_shapes
:
i
Placeholder_17Placeholder*#
_output_shapes
:џџџџџџџџџ*
dtype0*
shape:џџџџџџџџџ
S
AssignVariableOp_17AssignVariableOpmiddle_0/biasPlaceholder_17*
dtype0
r
ReadVariableOp_17ReadVariableOpmiddle_0/bias^AssignVariableOp_17*
dtype0*
_output_shapes	
:
З
Placeholder_18Placeholder*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
U
AssignVariableOp_18AssignVariableOpmiddle_2/kernelPlaceholder_18*
dtype0

ReadVariableOp_18ReadVariableOpmiddle_2/kernel^AssignVariableOp_18*(
_output_shapes
:*
dtype0
i
Placeholder_19Placeholder*
shape:џџџџџџџџџ*
dtype0*#
_output_shapes
:џџџџџџџџџ
S
AssignVariableOp_19AssignVariableOpmiddle_2/biasPlaceholder_19*
dtype0
r
ReadVariableOp_19ReadVariableOpmiddle_2/bias^AssignVariableOp_19*
_output_shapes	
:*
dtype0
З
Placeholder_20Placeholder*
dtype0*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
\
AssignVariableOp_20AssignVariableOpup_level_2_no_0/kernelPlaceholder_20*
dtype0

ReadVariableOp_20ReadVariableOpup_level_2_no_0/kernel^AssignVariableOp_20*(
_output_shapes
:*
dtype0
i
Placeholder_21Placeholder*#
_output_shapes
:џџџџџџџџџ*
dtype0*
shape:џџџџџџџџџ
Z
AssignVariableOp_21AssignVariableOpup_level_2_no_0/biasPlaceholder_21*
dtype0
y
ReadVariableOp_21ReadVariableOpup_level_2_no_0/bias^AssignVariableOp_21*
dtype0*
_output_shapes	
:
З
Placeholder_22Placeholder*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
\
AssignVariableOp_22AssignVariableOpup_level_2_no_2/kernelPlaceholder_22*
dtype0

ReadVariableOp_22ReadVariableOpup_level_2_no_2/kernel^AssignVariableOp_22*'
_output_shapes
:@*
dtype0
i
Placeholder_23Placeholder*
dtype0*
shape:џџџџџџџџџ*#
_output_shapes
:џџџџџџџџџ
Z
AssignVariableOp_23AssignVariableOpup_level_2_no_2/biasPlaceholder_23*
dtype0
x
ReadVariableOp_23ReadVariableOpup_level_2_no_2/bias^AssignVariableOp_23*
dtype0*
_output_shapes
:@
З
Placeholder_24Placeholder*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
\
AssignVariableOp_24AssignVariableOpup_level_1_no_0/kernelPlaceholder_24*
dtype0

ReadVariableOp_24ReadVariableOpup_level_1_no_0/kernel^AssignVariableOp_24*'
_output_shapes
:@*
dtype0
i
Placeholder_25Placeholder*#
_output_shapes
:џџџџџџџџџ*
dtype0*
shape:џџџџџџџџџ
Z
AssignVariableOp_25AssignVariableOpup_level_1_no_0/biasPlaceholder_25*
dtype0
x
ReadVariableOp_25ReadVariableOpup_level_1_no_0/bias^AssignVariableOp_25*
dtype0*
_output_shapes
:@
З
Placeholder_26Placeholder*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
\
AssignVariableOp_26AssignVariableOpup_level_1_no_2/kernelPlaceholder_26*
dtype0

ReadVariableOp_26ReadVariableOpup_level_1_no_2/kernel^AssignVariableOp_26*&
_output_shapes
:@ *
dtype0
i
Placeholder_27Placeholder*
dtype0*
shape:џџџџџџџџџ*#
_output_shapes
:џџџџџџџџџ
Z
AssignVariableOp_27AssignVariableOpup_level_1_no_2/biasPlaceholder_27*
dtype0
x
ReadVariableOp_27ReadVariableOpup_level_1_no_2/bias^AssignVariableOp_27*
_output_shapes
: *
dtype0
З
Placeholder_28Placeholder*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
\
AssignVariableOp_28AssignVariableOpup_level_0_no_0/kernelPlaceholder_28*
dtype0

ReadVariableOp_28ReadVariableOpup_level_0_no_0/kernel^AssignVariableOp_28*&
_output_shapes
:@ *
dtype0
i
Placeholder_29Placeholder*
shape:џџџџџџџџџ*#
_output_shapes
:џџџџџџџџџ*
dtype0
Z
AssignVariableOp_29AssignVariableOpup_level_0_no_0/biasPlaceholder_29*
dtype0
x
ReadVariableOp_29ReadVariableOpup_level_0_no_0/bias^AssignVariableOp_29*
_output_shapes
: *
dtype0
З
Placeholder_30Placeholder*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
\
AssignVariableOp_30AssignVariableOpup_level_0_no_2/kernelPlaceholder_30*
dtype0

ReadVariableOp_30ReadVariableOpup_level_0_no_2/kernel^AssignVariableOp_30*&
_output_shapes
:  *
dtype0
i
Placeholder_31Placeholder*#
_output_shapes
:џџџџџџџџџ*
dtype0*
shape:џџџџџџџџџ
Z
AssignVariableOp_31AssignVariableOpup_level_0_no_2/biasPlaceholder_31*
dtype0
x
ReadVariableOp_31ReadVariableOpup_level_0_no_2/bias^AssignVariableOp_31*
dtype0*
_output_shapes
: 
З
Placeholder_32Placeholder*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0
U
AssignVariableOp_32AssignVariableOpfeatures/kernelPlaceholder_32*
dtype0

ReadVariableOp_32ReadVariableOpfeatures/kernel^AssignVariableOp_32*
dtype0*'
_output_shapes
: 
i
Placeholder_33Placeholder*
dtype0*#
_output_shapes
:џџџџџџџџџ*
shape:џџџџџџџџџ
S
AssignVariableOp_33AssignVariableOpfeatures/biasPlaceholder_33*
dtype0
r
ReadVariableOp_33ReadVariableOpfeatures/bias^AssignVariableOp_33*
dtype0*
_output_shapes	
:
З
Placeholder_34Placeholder*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
Q
AssignVariableOp_34AssignVariableOpprob/kernelPlaceholder_34*
dtype0
|
ReadVariableOp_34ReadVariableOpprob/kernel^AssignVariableOp_34*
dtype0*'
_output_shapes
:
i
Placeholder_35Placeholder*#
_output_shapes
:џџџџџџџџџ*
shape:џџџџџџџџџ*
dtype0
O
AssignVariableOp_35AssignVariableOp	prob/biasPlaceholder_35*
dtype0
m
ReadVariableOp_35ReadVariableOp	prob/bias^AssignVariableOp_35*
_output_shapes
:*
dtype0
З
Placeholder_36Placeholder*
dtype0*J
_output_shapes8
6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ*?
shape6:4џџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџџ
Q
AssignVariableOp_36AssignVariableOpdist/kernelPlaceholder_36*
dtype0
|
ReadVariableOp_36ReadVariableOpdist/kernel^AssignVariableOp_36*
dtype0*'
_output_shapes
: 
i
Placeholder_37Placeholder*#
_output_shapes
:џџџџџџџџџ*
shape:џџџџџџџџџ*
dtype0
O
AssignVariableOp_37AssignVariableOp	dist/biasPlaceholder_37*
dtype0
m
ReadVariableOp_37ReadVariableOp	dist/bias^AssignVariableOp_37*
dtype0*
_output_shapes
: 
X
VarIsInitializedOpVarIsInitializedOpdown_level_0_no_1/bias*
_output_shapes
: 
Z
VarIsInitializedOp_1VarIsInitializedOpup_level_1_no_0/kernel*
_output_shapes
: 
X
VarIsInitializedOp_2VarIsInitializedOpup_level_1_no_0/bias*
_output_shapes
: 
M
VarIsInitializedOp_3VarIsInitializedOp	prob/bias*
_output_shapes
: 
O
VarIsInitializedOp_4VarIsInitializedOpdist/kernel*
_output_shapes
: 
\
VarIsInitializedOp_5VarIsInitializedOpdown_level_0_no_1/kernel*
_output_shapes
: 
Z
VarIsInitializedOp_6VarIsInitializedOpdown_level_1_no_1/bias*
_output_shapes
: 
Q
VarIsInitializedOp_7VarIsInitializedOpconv2d_2/bias*
_output_shapes
: 
Z
VarIsInitializedOp_8VarIsInitializedOpdown_level_2_no_0/bias*
_output_shapes
: 
Z
VarIsInitializedOp_9VarIsInitializedOpup_level_1_no_2/kernel*
_output_shapes
: 
T
VarIsInitializedOp_10VarIsInitializedOpfeatures/kernel*
_output_shapes
: 
N
VarIsInitializedOp_11VarIsInitializedOp	dist/bias*
_output_shapes
: 
]
VarIsInitializedOp_12VarIsInitializedOpdown_level_1_no_0/kernel*
_output_shapes
: 
]
VarIsInitializedOp_13VarIsInitializedOpdown_level_1_no_1/kernel*
_output_shapes
: 
]
VarIsInitializedOp_14VarIsInitializedOpdown_level_2_no_0/kernel*
_output_shapes
: 
[
VarIsInitializedOp_15VarIsInitializedOpup_level_2_no_0/kernel*
_output_shapes
: 
T
VarIsInitializedOp_16VarIsInitializedOpmiddle_0/kernel*
_output_shapes
: 
R
VarIsInitializedOp_17VarIsInitializedOpconv2d_1/bias*
_output_shapes
: 
R
VarIsInitializedOp_18VarIsInitializedOpmiddle_2/bias*
_output_shapes
: 
Y
VarIsInitializedOp_19VarIsInitializedOpup_level_2_no_0/bias*
_output_shapes
: 
Y
VarIsInitializedOp_20VarIsInitializedOpup_level_0_no_2/bias*
_output_shapes
: 
]
VarIsInitializedOp_21VarIsInitializedOpdown_level_2_no_1/kernel*
_output_shapes
: 
T
VarIsInitializedOp_22VarIsInitializedOpconv2d_2/kernel*
_output_shapes
: 
R
VarIsInitializedOp_23VarIsInitializedOpfeatures/bias*
_output_shapes
: 
R
VarIsInitializedOp_24VarIsInitializedOpmiddle_0/bias*
_output_shapes
: 
T
VarIsInitializedOp_25VarIsInitializedOpmiddle_2/kernel*
_output_shapes
: 
[
VarIsInitializedOp_26VarIsInitializedOpup_level_2_no_2/kernel*
_output_shapes
: 
Y
VarIsInitializedOp_27VarIsInitializedOpup_level_1_no_2/bias*
_output_shapes
: 
Y
VarIsInitializedOp_28VarIsInitializedOpup_level_0_no_0/bias*
_output_shapes
: 
[
VarIsInitializedOp_29VarIsInitializedOpdown_level_2_no_1/bias*
_output_shapes
: 
T
VarIsInitializedOp_30VarIsInitializedOpconv2d_1/kernel*
_output_shapes
: 
]
VarIsInitializedOp_31VarIsInitializedOpdown_level_0_no_0/kernel*
_output_shapes
: 
[
VarIsInitializedOp_32VarIsInitializedOpdown_level_0_no_0/bias*
_output_shapes
: 
[
VarIsInitializedOp_33VarIsInitializedOpdown_level_1_no_0/bias*
_output_shapes
: 
Y
VarIsInitializedOp_34VarIsInitializedOpup_level_2_no_2/bias*
_output_shapes
: 
[
VarIsInitializedOp_35VarIsInitializedOpup_level_0_no_0/kernel*
_output_shapes
: 
P
VarIsInitializedOp_36VarIsInitializedOpprob/kernel*
_output_shapes
: 
[
VarIsInitializedOp_37VarIsInitializedOpup_level_0_no_2/kernel*
_output_shapes
: 
Ь
initNoOp^conv2d_1/bias/Assign^conv2d_1/kernel/Assign^conv2d_2/bias/Assign^conv2d_2/kernel/Assign^dist/bias/Assign^dist/kernel/Assign^down_level_0_no_0/bias/Assign ^down_level_0_no_0/kernel/Assign^down_level_0_no_1/bias/Assign ^down_level_0_no_1/kernel/Assign^down_level_1_no_0/bias/Assign ^down_level_1_no_0/kernel/Assign^down_level_1_no_1/bias/Assign ^down_level_1_no_1/kernel/Assign^down_level_2_no_0/bias/Assign ^down_level_2_no_0/kernel/Assign^down_level_2_no_1/bias/Assign ^down_level_2_no_1/kernel/Assign^features/bias/Assign^features/kernel/Assign^middle_0/bias/Assign^middle_0/kernel/Assign^middle_2/bias/Assign^middle_2/kernel/Assign^prob/bias/Assign^prob/kernel/Assign^up_level_0_no_0/bias/Assign^up_level_0_no_0/kernel/Assign^up_level_0_no_2/bias/Assign^up_level_0_no_2/kernel/Assign^up_level_1_no_0/bias/Assign^up_level_1_no_0/kernel/Assign^up_level_1_no_2/bias/Assign^up_level_1_no_2/kernel/Assign^up_level_2_no_0/bias/Assign^up_level_2_no_0/kernel/Assign^up_level_2_no_2/bias/Assign^up_level_2_no_2/kernel/Assign
}
conv2d_transpose_1/ConstConst*%
valueB*  ?*&
_output_shapes
:*
dtype0
е
conv2d_transpose_1/kernelVarHandleOp*
dtype0*
_output_shapes
: *
	container *
shape:*,
_class"
 loc:@conv2d_transpose_1/kernel**
shared_nameconv2d_transpose_1/kernel

:conv2d_transpose_1/kernel/IsInitialized/VarIsInitializedOpVarIsInitializedOpconv2d_transpose_1/kernel*
_output_shapes
: 
v
 conv2d_transpose_1/kernel/AssignAssignVariableOpconv2d_transpose_1/kernelconv2d_transpose_1/Const*
dtype0

-conv2d_transpose_1/kernel/Read/ReadVariableOpReadVariableOpconv2d_transpose_1/kernel*
dtype0*&
_output_shapes
:
d
conv2d_transpose_1/ShapeShapeprob/Sigmoid*
T0*
_output_shapes
:*
out_type0
p
&conv2d_transpose_1/strided_slice/stackConst*
dtype0*
valueB: *
_output_shapes
:
r
(conv2d_transpose_1/strided_slice/stack_1Const*
_output_shapes
:*
valueB:*
dtype0
r
(conv2d_transpose_1/strided_slice/stack_2Const*
valueB:*
dtype0*
_output_shapes
:
и
 conv2d_transpose_1/strided_sliceStridedSliceconv2d_transpose_1/Shape&conv2d_transpose_1/strided_slice/stack(conv2d_transpose_1/strided_slice/stack_1(conv2d_transpose_1/strided_slice/stack_2*
ellipsis_mask *
_output_shapes
: *
shrink_axis_mask*
end_mask *
T0*
Index0*
new_axis_mask *

begin_mask 
r
(conv2d_transpose_1/strided_slice_1/stackConst*
valueB:*
dtype0*
_output_shapes
:
t
*conv2d_transpose_1/strided_slice_1/stack_1Const*
dtype0*
valueB:*
_output_shapes
:
t
*conv2d_transpose_1/strided_slice_1/stack_2Const*
dtype0*
valueB:*
_output_shapes
:
р
"conv2d_transpose_1/strided_slice_1StridedSliceconv2d_transpose_1/Shape(conv2d_transpose_1/strided_slice_1/stack*conv2d_transpose_1/strided_slice_1/stack_1*conv2d_transpose_1/strided_slice_1/stack_2*
end_mask *
_output_shapes
: *
new_axis_mask *
ellipsis_mask *
Index0*

begin_mask *
shrink_axis_mask*
T0
r
(conv2d_transpose_1/strided_slice_2/stackConst*
dtype0*
valueB:*
_output_shapes
:
t
*conv2d_transpose_1/strided_slice_2/stack_1Const*
valueB:*
_output_shapes
:*
dtype0
t
*conv2d_transpose_1/strided_slice_2/stack_2Const*
dtype0*
_output_shapes
:*
valueB:
р
"conv2d_transpose_1/strided_slice_2StridedSliceconv2d_transpose_1/Shape(conv2d_transpose_1/strided_slice_2/stack*conv2d_transpose_1/strided_slice_2/stack_1*conv2d_transpose_1/strided_slice_2/stack_2*
new_axis_mask *
T0*
shrink_axis_mask*
end_mask *
_output_shapes
: *
Index0*
ellipsis_mask *

begin_mask 
Z
conv2d_transpose_1/mul/yConst*
value	B :*
_output_shapes
: *
dtype0
|
conv2d_transpose_1/mulMul"conv2d_transpose_1/strided_slice_1conv2d_transpose_1/mul/y*
_output_shapes
: *
T0
\
conv2d_transpose_1/mul_1/yConst*
_output_shapes
: *
value	B :*
dtype0

conv2d_transpose_1/mul_1Mul"conv2d_transpose_1/strided_slice_2conv2d_transpose_1/mul_1/y*
T0*
_output_shapes
: 
\
conv2d_transpose_1/stack/3Const*
dtype0*
value	B :*
_output_shapes
: 
Ъ
conv2d_transpose_1/stackPack conv2d_transpose_1/strided_sliceconv2d_transpose_1/mulconv2d_transpose_1/mul_1conv2d_transpose_1/stack/3*
_output_shapes
:*
T0*
N*

axis 

2conv2d_transpose_1/conv2d_transpose/ReadVariableOpReadVariableOpconv2d_transpose_1/kernel*
dtype0*&
_output_shapes
:
щ
#conv2d_transpose_1/conv2d_transposeConv2DBackpropInputconv2d_transpose_1/stack2conv2d_transpose_1/conv2d_transpose/ReadVariableOpprob/Sigmoid*
explicit_paddings
 *
data_formatNHWC*
T0*
paddingSAME*
	dilations
*
strides
*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ*
use_cudnn_on_gpu(
a
up_sampling2d_4/ShapeShapedist/BiasAdd*
T0*
_output_shapes
:*
out_type0
m
#up_sampling2d_4/strided_slice/stackConst*
_output_shapes
:*
valueB:*
dtype0
o
%up_sampling2d_4/strided_slice/stack_1Const*
valueB:*
_output_shapes
:*
dtype0
o
%up_sampling2d_4/strided_slice/stack_2Const*
_output_shapes
:*
valueB:*
dtype0
Э
up_sampling2d_4/strided_sliceStridedSliceup_sampling2d_4/Shape#up_sampling2d_4/strided_slice/stack%up_sampling2d_4/strided_slice/stack_1%up_sampling2d_4/strided_slice/stack_2*
_output_shapes
:*
T0*
new_axis_mask *
Index0*
ellipsis_mask *

begin_mask *
shrink_axis_mask *
end_mask 
f
up_sampling2d_4/ConstConst*
dtype0*
_output_shapes
:*
valueB"      
u
up_sampling2d_4/mulMulup_sampling2d_4/strided_sliceup_sampling2d_4/Const*
T0*
_output_shapes
:
м
%up_sampling2d_4/ResizeNearestNeighborResizeNearestNeighbordist/BiasAddup_sampling2d_4/mul*
align_corners( *A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ *
T0*
half_pixel_centers( 
[
concatenate_4/concat/axisConst*
dtype0*
value	B :*
_output_shapes
: 
ш
concatenate_4/concatConcatV2#conv2d_transpose_1/conv2d_transpose%up_sampling2d_4/ResizeNearestNeighborconcatenate_4/concat/axis*
T0*

Tidx0*A
_output_shapes/
-:+џџџџџџџџџџџџџџџџџџџџџџџџџџџ!*
N
^
VarIsInitializedOp_38VarIsInitializedOpconv2d_transpose_1/kernel*
_output_shapes
: 
1
init_1NoOp!^conv2d_transpose_1/kernel/Assign
Y
save/filename/inputConst*
dtype0*
valueB Bmodel*
_output_shapes
: 
n
save/filenamePlaceholderWithDefaultsave/filename/input*
shape: *
dtype0*
_output_shapes
: 
e

save/ConstPlaceholderWithDefaultsave/filename*
dtype0*
_output_shapes
: *
shape: 

save/StringJoin/inputs_1Const*<
value3B1 B+_temp_7ca0f0b83b6e4ab287fc607c9a9622d2/part*
dtype0*
_output_shapes
: 
u
save/StringJoin
StringJoin
save/Constsave/StringJoin/inputs_1*
	separator *
_output_shapes
: *
N
Q
save/num_shardsConst*
value	B :*
dtype0*
_output_shapes
: 
k
save/ShardedFilename/shardConst"/device:CPU:0*
dtype0*
_output_shapes
: *
value	B : 

save/ShardedFilenameShardedFilenamesave/StringJoinsave/ShardedFilename/shardsave/num_shards"/device:CPU:0*
_output_shapes
: 

save/SaveV2/tensor_namesConst"/device:CPU:0*Р
valueЖBГ'Bconv2d_1/biasBconv2d_1/kernelBconv2d_2/biasBconv2d_2/kernelBconv2d_transpose_1/kernelB	dist/biasBdist/kernelBdown_level_0_no_0/biasBdown_level_0_no_0/kernelBdown_level_0_no_1/biasBdown_level_0_no_1/kernelBdown_level_1_no_0/biasBdown_level_1_no_0/kernelBdown_level_1_no_1/biasBdown_level_1_no_1/kernelBdown_level_2_no_0/biasBdown_level_2_no_0/kernelBdown_level_2_no_1/biasBdown_level_2_no_1/kernelBfeatures/biasBfeatures/kernelBmiddle_0/biasBmiddle_0/kernelBmiddle_2/biasBmiddle_2/kernelB	prob/biasBprob/kernelBup_level_0_no_0/biasBup_level_0_no_0/kernelBup_level_0_no_2/biasBup_level_0_no_2/kernelBup_level_1_no_0/biasBup_level_1_no_0/kernelBup_level_1_no_2/biasBup_level_1_no_2/kernelBup_level_2_no_0/biasBup_level_2_no_0/kernelBup_level_2_no_2/biasBup_level_2_no_2/kernel*
_output_shapes
:'*
dtype0
Р
save/SaveV2/shape_and_slicesConst"/device:CPU:0*
_output_shapes
:'*
dtype0*a
valueXBV'B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B 
р
save/SaveV2SaveV2save/ShardedFilenamesave/SaveV2/tensor_namessave/SaveV2/shape_and_slices!conv2d_1/bias/Read/ReadVariableOp#conv2d_1/kernel/Read/ReadVariableOp!conv2d_2/bias/Read/ReadVariableOp#conv2d_2/kernel/Read/ReadVariableOp-conv2d_transpose_1/kernel/Read/ReadVariableOpdist/bias/Read/ReadVariableOpdist/kernel/Read/ReadVariableOp*down_level_0_no_0/bias/Read/ReadVariableOp,down_level_0_no_0/kernel/Read/ReadVariableOp*down_level_0_no_1/bias/Read/ReadVariableOp,down_level_0_no_1/kernel/Read/ReadVariableOp*down_level_1_no_0/bias/Read/ReadVariableOp,down_level_1_no_0/kernel/Read/ReadVariableOp*down_level_1_no_1/bias/Read/ReadVariableOp,down_level_1_no_1/kernel/Read/ReadVariableOp*down_level_2_no_0/bias/Read/ReadVariableOp,down_level_2_no_0/kernel/Read/ReadVariableOp*down_level_2_no_1/bias/Read/ReadVariableOp,down_level_2_no_1/kernel/Read/ReadVariableOp!features/bias/Read/ReadVariableOp#features/kernel/Read/ReadVariableOp!middle_0/bias/Read/ReadVariableOp#middle_0/kernel/Read/ReadVariableOp!middle_2/bias/Read/ReadVariableOp#middle_2/kernel/Read/ReadVariableOpprob/bias/Read/ReadVariableOpprob/kernel/Read/ReadVariableOp(up_level_0_no_0/bias/Read/ReadVariableOp*up_level_0_no_0/kernel/Read/ReadVariableOp(up_level_0_no_2/bias/Read/ReadVariableOp*up_level_0_no_2/kernel/Read/ReadVariableOp(up_level_1_no_0/bias/Read/ReadVariableOp*up_level_1_no_0/kernel/Read/ReadVariableOp(up_level_1_no_2/bias/Read/ReadVariableOp*up_level_1_no_2/kernel/Read/ReadVariableOp(up_level_2_no_0/bias/Read/ReadVariableOp*up_level_2_no_0/kernel/Read/ReadVariableOp(up_level_2_no_2/bias/Read/ReadVariableOp*up_level_2_no_2/kernel/Read/ReadVariableOp"/device:CPU:0*5
dtypes+
)2'
 
save/control_dependencyIdentitysave/ShardedFilename^save/SaveV2"/device:CPU:0*
_output_shapes
: *
T0*'
_class
loc:@save/ShardedFilename
Ќ
+save/MergeV2Checkpoints/checkpoint_prefixesPacksave/ShardedFilename^save/control_dependency"/device:CPU:0*
T0*
N*
_output_shapes
:*

axis 

save/MergeV2CheckpointsMergeV2Checkpoints+save/MergeV2Checkpoints/checkpoint_prefixes
save/Const"/device:CPU:0*
delete_old_dirs(

save/IdentityIdentity
save/Const^save/MergeV2Checkpoints^save/control_dependency"/device:CPU:0*
_output_shapes
: *
T0

save/RestoreV2/tensor_namesConst"/device:CPU:0*Р
valueЖBГ'Bconv2d_1/biasBconv2d_1/kernelBconv2d_2/biasBconv2d_2/kernelBconv2d_transpose_1/kernelB	dist/biasBdist/kernelBdown_level_0_no_0/biasBdown_level_0_no_0/kernelBdown_level_0_no_1/biasBdown_level_0_no_1/kernelBdown_level_1_no_0/biasBdown_level_1_no_0/kernelBdown_level_1_no_1/biasBdown_level_1_no_1/kernelBdown_level_2_no_0/biasBdown_level_2_no_0/kernelBdown_level_2_no_1/biasBdown_level_2_no_1/kernelBfeatures/biasBfeatures/kernelBmiddle_0/biasBmiddle_0/kernelBmiddle_2/biasBmiddle_2/kernelB	prob/biasBprob/kernelBup_level_0_no_0/biasBup_level_0_no_0/kernelBup_level_0_no_2/biasBup_level_0_no_2/kernelBup_level_1_no_0/biasBup_level_1_no_0/kernelBup_level_1_no_2/biasBup_level_1_no_2/kernelBup_level_2_no_0/biasBup_level_2_no_0/kernelBup_level_2_no_2/biasBup_level_2_no_2/kernel*
dtype0*
_output_shapes
:'
У
save/RestoreV2/shape_and_slicesConst"/device:CPU:0*a
valueXBV'B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B B *
_output_shapes
:'*
dtype0
р
save/RestoreV2	RestoreV2
save/Constsave/RestoreV2/tensor_namessave/RestoreV2/shape_and_slices"/device:CPU:0*5
dtypes+
)2'*В
_output_shapes
:::::::::::::::::::::::::::::::::::::::
N
save/Identity_1Identitysave/RestoreV2*
_output_shapes
:*
T0
V
save/AssignVariableOpAssignVariableOpconv2d_1/biassave/Identity_1*
dtype0
P
save/Identity_2Identitysave/RestoreV2:1*
_output_shapes
:*
T0
Z
save/AssignVariableOp_1AssignVariableOpconv2d_1/kernelsave/Identity_2*
dtype0
P
save/Identity_3Identitysave/RestoreV2:2*
_output_shapes
:*
T0
X
save/AssignVariableOp_2AssignVariableOpconv2d_2/biassave/Identity_3*
dtype0
P
save/Identity_4Identitysave/RestoreV2:3*
_output_shapes
:*
T0
Z
save/AssignVariableOp_3AssignVariableOpconv2d_2/kernelsave/Identity_4*
dtype0
P
save/Identity_5Identitysave/RestoreV2:4*
_output_shapes
:*
T0
d
save/AssignVariableOp_4AssignVariableOpconv2d_transpose_1/kernelsave/Identity_5*
dtype0
P
save/Identity_6Identitysave/RestoreV2:5*
T0*
_output_shapes
:
T
save/AssignVariableOp_5AssignVariableOp	dist/biassave/Identity_6*
dtype0
P
save/Identity_7Identitysave/RestoreV2:6*
T0*
_output_shapes
:
V
save/AssignVariableOp_6AssignVariableOpdist/kernelsave/Identity_7*
dtype0
P
save/Identity_8Identitysave/RestoreV2:7*
T0*
_output_shapes
:
a
save/AssignVariableOp_7AssignVariableOpdown_level_0_no_0/biassave/Identity_8*
dtype0
P
save/Identity_9Identitysave/RestoreV2:8*
_output_shapes
:*
T0
c
save/AssignVariableOp_8AssignVariableOpdown_level_0_no_0/kernelsave/Identity_9*
dtype0
Q
save/Identity_10Identitysave/RestoreV2:9*
_output_shapes
:*
T0
b
save/AssignVariableOp_9AssignVariableOpdown_level_0_no_1/biassave/Identity_10*
dtype0
R
save/Identity_11Identitysave/RestoreV2:10*
T0*
_output_shapes
:
e
save/AssignVariableOp_10AssignVariableOpdown_level_0_no_1/kernelsave/Identity_11*
dtype0
R
save/Identity_12Identitysave/RestoreV2:11*
_output_shapes
:*
T0
c
save/AssignVariableOp_11AssignVariableOpdown_level_1_no_0/biassave/Identity_12*
dtype0
R
save/Identity_13Identitysave/RestoreV2:12*
_output_shapes
:*
T0
e
save/AssignVariableOp_12AssignVariableOpdown_level_1_no_0/kernelsave/Identity_13*
dtype0
R
save/Identity_14Identitysave/RestoreV2:13*
_output_shapes
:*
T0
c
save/AssignVariableOp_13AssignVariableOpdown_level_1_no_1/biassave/Identity_14*
dtype0
R
save/Identity_15Identitysave/RestoreV2:14*
_output_shapes
:*
T0
e
save/AssignVariableOp_14AssignVariableOpdown_level_1_no_1/kernelsave/Identity_15*
dtype0
R
save/Identity_16Identitysave/RestoreV2:15*
T0*
_output_shapes
:
c
save/AssignVariableOp_15AssignVariableOpdown_level_2_no_0/biassave/Identity_16*
dtype0
R
save/Identity_17Identitysave/RestoreV2:16*
_output_shapes
:*
T0
e
save/AssignVariableOp_16AssignVariableOpdown_level_2_no_0/kernelsave/Identity_17*
dtype0
R
save/Identity_18Identitysave/RestoreV2:17*
_output_shapes
:*
T0
c
save/AssignVariableOp_17AssignVariableOpdown_level_2_no_1/biassave/Identity_18*
dtype0
R
save/Identity_19Identitysave/RestoreV2:18*
T0*
_output_shapes
:
e
save/AssignVariableOp_18AssignVariableOpdown_level_2_no_1/kernelsave/Identity_19*
dtype0
R
save/Identity_20Identitysave/RestoreV2:19*
_output_shapes
:*
T0
Z
save/AssignVariableOp_19AssignVariableOpfeatures/biassave/Identity_20*
dtype0
R
save/Identity_21Identitysave/RestoreV2:20*
_output_shapes
:*
T0
\
save/AssignVariableOp_20AssignVariableOpfeatures/kernelsave/Identity_21*
dtype0
R
save/Identity_22Identitysave/RestoreV2:21*
_output_shapes
:*
T0
Z
save/AssignVariableOp_21AssignVariableOpmiddle_0/biassave/Identity_22*
dtype0
R
save/Identity_23Identitysave/RestoreV2:22*
T0*
_output_shapes
:
\
save/AssignVariableOp_22AssignVariableOpmiddle_0/kernelsave/Identity_23*
dtype0
R
save/Identity_24Identitysave/RestoreV2:23*
_output_shapes
:*
T0
Z
save/AssignVariableOp_23AssignVariableOpmiddle_2/biassave/Identity_24*
dtype0
R
save/Identity_25Identitysave/RestoreV2:24*
T0*
_output_shapes
:
\
save/AssignVariableOp_24AssignVariableOpmiddle_2/kernelsave/Identity_25*
dtype0
R
save/Identity_26Identitysave/RestoreV2:25*
_output_shapes
:*
T0
V
save/AssignVariableOp_25AssignVariableOp	prob/biassave/Identity_26*
dtype0
R
save/Identity_27Identitysave/RestoreV2:26*
T0*
_output_shapes
:
X
save/AssignVariableOp_26AssignVariableOpprob/kernelsave/Identity_27*
dtype0
R
save/Identity_28Identitysave/RestoreV2:27*
T0*
_output_shapes
:
a
save/AssignVariableOp_27AssignVariableOpup_level_0_no_0/biassave/Identity_28*
dtype0
R
save/Identity_29Identitysave/RestoreV2:28*
_output_shapes
:*
T0
c
save/AssignVariableOp_28AssignVariableOpup_level_0_no_0/kernelsave/Identity_29*
dtype0
R
save/Identity_30Identitysave/RestoreV2:29*
T0*
_output_shapes
:
a
save/AssignVariableOp_29AssignVariableOpup_level_0_no_2/biassave/Identity_30*
dtype0
R
save/Identity_31Identitysave/RestoreV2:30*
_output_shapes
:*
T0
c
save/AssignVariableOp_30AssignVariableOpup_level_0_no_2/kernelsave/Identity_31*
dtype0
R
save/Identity_32Identitysave/RestoreV2:31*
T0*
_output_shapes
:
a
save/AssignVariableOp_31AssignVariableOpup_level_1_no_0/biassave/Identity_32*
dtype0
R
save/Identity_33Identitysave/RestoreV2:32*
T0*
_output_shapes
:
c
save/AssignVariableOp_32AssignVariableOpup_level_1_no_0/kernelsave/Identity_33*
dtype0
R
save/Identity_34Identitysave/RestoreV2:33*
T0*
_output_shapes
:
a
save/AssignVariableOp_33AssignVariableOpup_level_1_no_2/biassave/Identity_34*
dtype0
R
save/Identity_35Identitysave/RestoreV2:34*
_output_shapes
:*
T0
c
save/AssignVariableOp_34AssignVariableOpup_level_1_no_2/kernelsave/Identity_35*
dtype0
R
save/Identity_36Identitysave/RestoreV2:35*
_output_shapes
:*
T0
a
save/AssignVariableOp_35AssignVariableOpup_level_2_no_0/biassave/Identity_36*
dtype0
R
save/Identity_37Identitysave/RestoreV2:36*
T0*
_output_shapes
:
c
save/AssignVariableOp_36AssignVariableOpup_level_2_no_0/kernelsave/Identity_37*
dtype0
R
save/Identity_38Identitysave/RestoreV2:37*
_output_shapes
:*
T0
a
save/AssignVariableOp_37AssignVariableOpup_level_2_no_2/biassave/Identity_38*
dtype0
R
save/Identity_39Identitysave/RestoreV2:38*
T0*
_output_shapes
:
c
save/AssignVariableOp_38AssignVariableOpup_level_2_no_2/kernelsave/Identity_39*
dtype0
Ћ
save/restore_shardNoOp^save/AssignVariableOp^save/AssignVariableOp_1^save/AssignVariableOp_10^save/AssignVariableOp_11^save/AssignVariableOp_12^save/AssignVariableOp_13^save/AssignVariableOp_14^save/AssignVariableOp_15^save/AssignVariableOp_16^save/AssignVariableOp_17^save/AssignVariableOp_18^save/AssignVariableOp_19^save/AssignVariableOp_2^save/AssignVariableOp_20^save/AssignVariableOp_21^save/AssignVariableOp_22^save/AssignVariableOp_23^save/AssignVariableOp_24^save/AssignVariableOp_25^save/AssignVariableOp_26^save/AssignVariableOp_27^save/AssignVariableOp_28^save/AssignVariableOp_29^save/AssignVariableOp_3^save/AssignVariableOp_30^save/AssignVariableOp_31^save/AssignVariableOp_32^save/AssignVariableOp_33^save/AssignVariableOp_34^save/AssignVariableOp_35^save/AssignVariableOp_36^save/AssignVariableOp_37^save/AssignVariableOp_38^save/AssignVariableOp_4^save/AssignVariableOp_5^save/AssignVariableOp_6^save/AssignVariableOp_7^save/AssignVariableOp_8^save/AssignVariableOp_9
-
save/restore_allNoOp^save/restore_shard"<
save/Const:0save/Identity:0save/restore_all (5 @F8"ѕ&
trainable_variablesн&к&
q
conv2d_1/kernel:0conv2d_1/kernel/Assign%conv2d_1/kernel/Read/ReadVariableOp:0(2conv2d_1/random_uniform:08
b
conv2d_1/bias:0conv2d_1/bias/Assign#conv2d_1/bias/Read/ReadVariableOp:0(2conv2d_1/Const:08
q
conv2d_2/kernel:0conv2d_2/kernel/Assign%conv2d_2/kernel/Read/ReadVariableOp:0(2conv2d_2/random_uniform:08
b
conv2d_2/bias:0conv2d_2/bias/Assign#conv2d_2/bias/Read/ReadVariableOp:0(2conv2d_2/Const:08

down_level_0_no_0/kernel:0down_level_0_no_0/kernel/Assign.down_level_0_no_0/kernel/Read/ReadVariableOp:0(2"down_level_0_no_0/random_uniform:08

down_level_0_no_0/bias:0down_level_0_no_0/bias/Assign,down_level_0_no_0/bias/Read/ReadVariableOp:0(2down_level_0_no_0/Const:08

down_level_0_no_1/kernel:0down_level_0_no_1/kernel/Assign.down_level_0_no_1/kernel/Read/ReadVariableOp:0(2"down_level_0_no_1/random_uniform:08

down_level_0_no_1/bias:0down_level_0_no_1/bias/Assign,down_level_0_no_1/bias/Read/ReadVariableOp:0(2down_level_0_no_1/Const:08

down_level_1_no_0/kernel:0down_level_1_no_0/kernel/Assign.down_level_1_no_0/kernel/Read/ReadVariableOp:0(2"down_level_1_no_0/random_uniform:08

down_level_1_no_0/bias:0down_level_1_no_0/bias/Assign,down_level_1_no_0/bias/Read/ReadVariableOp:0(2down_level_1_no_0/Const:08

down_level_1_no_1/kernel:0down_level_1_no_1/kernel/Assign.down_level_1_no_1/kernel/Read/ReadVariableOp:0(2"down_level_1_no_1/random_uniform:08

down_level_1_no_1/bias:0down_level_1_no_1/bias/Assign,down_level_1_no_1/bias/Read/ReadVariableOp:0(2down_level_1_no_1/Const:08

down_level_2_no_0/kernel:0down_level_2_no_0/kernel/Assign.down_level_2_no_0/kernel/Read/ReadVariableOp:0(2"down_level_2_no_0/random_uniform:08

down_level_2_no_0/bias:0down_level_2_no_0/bias/Assign,down_level_2_no_0/bias/Read/ReadVariableOp:0(2down_level_2_no_0/Const:08

down_level_2_no_1/kernel:0down_level_2_no_1/kernel/Assign.down_level_2_no_1/kernel/Read/ReadVariableOp:0(2"down_level_2_no_1/random_uniform:08

down_level_2_no_1/bias:0down_level_2_no_1/bias/Assign,down_level_2_no_1/bias/Read/ReadVariableOp:0(2down_level_2_no_1/Const:08
q
middle_0/kernel:0middle_0/kernel/Assign%middle_0/kernel/Read/ReadVariableOp:0(2middle_0/random_uniform:08
b
middle_0/bias:0middle_0/bias/Assign#middle_0/bias/Read/ReadVariableOp:0(2middle_0/Const:08
q
middle_2/kernel:0middle_2/kernel/Assign%middle_2/kernel/Read/ReadVariableOp:0(2middle_2/random_uniform:08
b
middle_2/bias:0middle_2/bias/Assign#middle_2/bias/Read/ReadVariableOp:0(2middle_2/Const:08

up_level_2_no_0/kernel:0up_level_2_no_0/kernel/Assign,up_level_2_no_0/kernel/Read/ReadVariableOp:0(2 up_level_2_no_0/random_uniform:08
~
up_level_2_no_0/bias:0up_level_2_no_0/bias/Assign*up_level_2_no_0/bias/Read/ReadVariableOp:0(2up_level_2_no_0/Const:08

up_level_2_no_2/kernel:0up_level_2_no_2/kernel/Assign,up_level_2_no_2/kernel/Read/ReadVariableOp:0(2 up_level_2_no_2/random_uniform:08
~
up_level_2_no_2/bias:0up_level_2_no_2/bias/Assign*up_level_2_no_2/bias/Read/ReadVariableOp:0(2up_level_2_no_2/Const:08

up_level_1_no_0/kernel:0up_level_1_no_0/kernel/Assign,up_level_1_no_0/kernel/Read/ReadVariableOp:0(2 up_level_1_no_0/random_uniform:08
~
up_level_1_no_0/bias:0up_level_1_no_0/bias/Assign*up_level_1_no_0/bias/Read/ReadVariableOp:0(2up_level_1_no_0/Const:08

up_level_1_no_2/kernel:0up_level_1_no_2/kernel/Assign,up_level_1_no_2/kernel/Read/ReadVariableOp:0(2 up_level_1_no_2/random_uniform:08
~
up_level_1_no_2/bias:0up_level_1_no_2/bias/Assign*up_level_1_no_2/bias/Read/ReadVariableOp:0(2up_level_1_no_2/Const:08

up_level_0_no_0/kernel:0up_level_0_no_0/kernel/Assign,up_level_0_no_0/kernel/Read/ReadVariableOp:0(2 up_level_0_no_0/random_uniform:08
~
up_level_0_no_0/bias:0up_level_0_no_0/bias/Assign*up_level_0_no_0/bias/Read/ReadVariableOp:0(2up_level_0_no_0/Const:08

up_level_0_no_2/kernel:0up_level_0_no_2/kernel/Assign,up_level_0_no_2/kernel/Read/ReadVariableOp:0(2 up_level_0_no_2/random_uniform:08
~
up_level_0_no_2/bias:0up_level_0_no_2/bias/Assign*up_level_0_no_2/bias/Read/ReadVariableOp:0(2up_level_0_no_2/Const:08
q
features/kernel:0features/kernel/Assign%features/kernel/Read/ReadVariableOp:0(2features/random_uniform:08
b
features/bias:0features/bias/Assign#features/bias/Read/ReadVariableOp:0(2features/Const:08
a
prob/kernel:0prob/kernel/Assign!prob/kernel/Read/ReadVariableOp:0(2prob/random_uniform:08
R
prob/bias:0prob/bias/Assignprob/bias/Read/ReadVariableOp:0(2prob/Const:08
a
dist/kernel:0dist/kernel/Assign!dist/kernel/Read/ReadVariableOp:0(2dist/random_uniform:08
R
dist/bias:0dist/bias/Assigndist/bias/Read/ReadVariableOp:0(2dist/Const:08

conv2d_transpose_1/kernel:0 conv2d_transpose_1/kernel/Assign/conv2d_transpose_1/kernel/Read/ReadVariableOp:0(2conv2d_transpose_1/Const:08"ы&
	variablesн&к&
q
conv2d_1/kernel:0conv2d_1/kernel/Assign%conv2d_1/kernel/Read/ReadVariableOp:0(2conv2d_1/random_uniform:08
b
conv2d_1/bias:0conv2d_1/bias/Assign#conv2d_1/bias/Read/ReadVariableOp:0(2conv2d_1/Const:08
q
conv2d_2/kernel:0conv2d_2/kernel/Assign%conv2d_2/kernel/Read/ReadVariableOp:0(2conv2d_2/random_uniform:08
b
conv2d_2/bias:0conv2d_2/bias/Assign#conv2d_2/bias/Read/ReadVariableOp:0(2conv2d_2/Const:08

down_level_0_no_0/kernel:0down_level_0_no_0/kernel/Assign.down_level_0_no_0/kernel/Read/ReadVariableOp:0(2"down_level_0_no_0/random_uniform:08

down_level_0_no_0/bias:0down_level_0_no_0/bias/Assign,down_level_0_no_0/bias/Read/ReadVariableOp:0(2down_level_0_no_0/Const:08

down_level_0_no_1/kernel:0down_level_0_no_1/kernel/Assign.down_level_0_no_1/kernel/Read/ReadVariableOp:0(2"down_level_0_no_1/random_uniform:08

down_level_0_no_1/bias:0down_level_0_no_1/bias/Assign,down_level_0_no_1/bias/Read/ReadVariableOp:0(2down_level_0_no_1/Const:08

down_level_1_no_0/kernel:0down_level_1_no_0/kernel/Assign.down_level_1_no_0/kernel/Read/ReadVariableOp:0(2"down_level_1_no_0/random_uniform:08

down_level_1_no_0/bias:0down_level_1_no_0/bias/Assign,down_level_1_no_0/bias/Read/ReadVariableOp:0(2down_level_1_no_0/Const:08

down_level_1_no_1/kernel:0down_level_1_no_1/kernel/Assign.down_level_1_no_1/kernel/Read/ReadVariableOp:0(2"down_level_1_no_1/random_uniform:08

down_level_1_no_1/bias:0down_level_1_no_1/bias/Assign,down_level_1_no_1/bias/Read/ReadVariableOp:0(2down_level_1_no_1/Const:08

down_level_2_no_0/kernel:0down_level_2_no_0/kernel/Assign.down_level_2_no_0/kernel/Read/ReadVariableOp:0(2"down_level_2_no_0/random_uniform:08

down_level_2_no_0/bias:0down_level_2_no_0/bias/Assign,down_level_2_no_0/bias/Read/ReadVariableOp:0(2down_level_2_no_0/Const:08

down_level_2_no_1/kernel:0down_level_2_no_1/kernel/Assign.down_level_2_no_1/kernel/Read/ReadVariableOp:0(2"down_level_2_no_1/random_uniform:08

down_level_2_no_1/bias:0down_level_2_no_1/bias/Assign,down_level_2_no_1/bias/Read/ReadVariableOp:0(2down_level_2_no_1/Const:08
q
middle_0/kernel:0middle_0/kernel/Assign%middle_0/kernel/Read/ReadVariableOp:0(2middle_0/random_uniform:08
b
middle_0/bias:0middle_0/bias/Assign#middle_0/bias/Read/ReadVariableOp:0(2middle_0/Const:08
q
middle_2/kernel:0middle_2/kernel/Assign%middle_2/kernel/Read/ReadVariableOp:0(2middle_2/random_uniform:08
b
middle_2/bias:0middle_2/bias/Assign#middle_2/bias/Read/ReadVariableOp:0(2middle_2/Const:08

up_level_2_no_0/kernel:0up_level_2_no_0/kernel/Assign,up_level_2_no_0/kernel/Read/ReadVariableOp:0(2 up_level_2_no_0/random_uniform:08
~
up_level_2_no_0/bias:0up_level_2_no_0/bias/Assign*up_level_2_no_0/bias/Read/ReadVariableOp:0(2up_level_2_no_0/Const:08

up_level_2_no_2/kernel:0up_level_2_no_2/kernel/Assign,up_level_2_no_2/kernel/Read/ReadVariableOp:0(2 up_level_2_no_2/random_uniform:08
~
up_level_2_no_2/bias:0up_level_2_no_2/bias/Assign*up_level_2_no_2/bias/Read/ReadVariableOp:0(2up_level_2_no_2/Const:08

up_level_1_no_0/kernel:0up_level_1_no_0/kernel/Assign,up_level_1_no_0/kernel/Read/ReadVariableOp:0(2 up_level_1_no_0/random_uniform:08
~
up_level_1_no_0/bias:0up_level_1_no_0/bias/Assign*up_level_1_no_0/bias/Read/ReadVariableOp:0(2up_level_1_no_0/Const:08

up_level_1_no_2/kernel:0up_level_1_no_2/kernel/Assign,up_level_1_no_2/kernel/Read/ReadVariableOp:0(2 up_level_1_no_2/random_uniform:08
~
up_level_1_no_2/bias:0up_level_1_no_2/bias/Assign*up_level_1_no_2/bias/Read/ReadVariableOp:0(2up_level_1_no_2/Const:08

up_level_0_no_0/kernel:0up_level_0_no_0/kernel/Assign,up_level_0_no_0/kernel/Read/ReadVariableOp:0(2 up_level_0_no_0/random_uniform:08
~
up_level_0_no_0/bias:0up_level_0_no_0/bias/Assign*up_level_0_no_0/bias/Read/ReadVariableOp:0(2up_level_0_no_0/Const:08

up_level_0_no_2/kernel:0up_level_0_no_2/kernel/Assign,up_level_0_no_2/kernel/Read/ReadVariableOp:0(2 up_level_0_no_2/random_uniform:08
~
up_level_0_no_2/bias:0up_level_0_no_2/bias/Assign*up_level_0_no_2/bias/Read/ReadVariableOp:0(2up_level_0_no_2/Const:08
q
features/kernel:0features/kernel/Assign%features/kernel/Read/ReadVariableOp:0(2features/random_uniform:08
b
features/bias:0features/bias/Assign#features/bias/Read/ReadVariableOp:0(2features/Const:08
a
prob/kernel:0prob/kernel/Assign!prob/kernel/Read/ReadVariableOp:0(2prob/random_uniform:08
R
prob/bias:0prob/bias/Assignprob/bias/Read/ReadVariableOp:0(2prob/Const:08
a
dist/kernel:0dist/kernel/Assign!dist/kernel/Read/ReadVariableOp:0(2dist/random_uniform:08
R
dist/bias:0dist/bias/Assigndist/bias/Read/ReadVariableOp:0(2dist/Const:08

conv2d_transpose_1/kernel:0 conv2d_transpose_1/kernel/Assign/conv2d_transpose_1/kernel/Read/ReadVariableOp:0(2conv2d_transpose_1/Const:08*Ц
serving_defaultВ
A
input8
input:0+џџџџџџџџџџџџџџџџџџџџџџџџџџџQ
outputG
concatenate_4/concat:0+џџџџџџџџџџџџџџџџџџџџџџџџџџџ!tensorflow/serving/predict