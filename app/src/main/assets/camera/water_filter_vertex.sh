attribute vec4 vPosition;
uniform mat4 vMatrix;
attribute vec4 aTextureCoord;
varying vec2 textureCoordinate;
varying vec2 vTextureCoord

void main(){
    gl_Position = vMatrix*vPosition;
    vTextureCoord = aTextureCoord.xy;
}