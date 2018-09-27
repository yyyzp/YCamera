#extension GL_OES_EGL_image_external : require
precision mediump float;
varying vec2 textureCoordinate;
uniform samplerExternalOES vTexture;
const mediump vec3 luminanceWeighting =vec3(0.2125, 0.7154, 0.0721);
const lowp float saturation=0.1;
void main() {
   vec4 textureColor= texture2D( vTexture, textureCoordinate );
   float luminance= dot(textureColor.rgb,luminanceWeighting);
   vec3 grayScaleColor=vec3(luminance);
   gl_FragColor=vec4(mix(grayScaleColor,textureColor.rgb,saturation),textureColor.w);
}