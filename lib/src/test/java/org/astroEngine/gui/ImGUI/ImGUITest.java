package org.astroEngine.gui.ImGUI;

import imgui.ImFont;
import imgui.ImGuiIO;
import imgui.internal.ImGui;
import org.astroEngine.AEWindow;
import org.astroEngine.GUI.ImGUIObject;
import org.astroEngine.util.Files;
import org.junit.jupiter.api.Test;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.system.MemoryUtil;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.openal.ALC10.*;

import java.awt.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class ImGUITest extends AEWindow {
  ImGUIObject object;
  ImFont font;
  long context, device;
  IntBuffer channels, sampleRate;
  ShortBuffer pcm;
  int source, buffer;

  public ImGUITest() {
    super(new Dimension(800, 600), "ImGui");

    object = new ImGUIObject(() -> {

      ImGui.begin("MyWindow");

      ImGui.text("Manas");

      ImGui.pushFont(font);
      ImGui.text("JetBrains");
      ImGui.popFont();

      ImGui.end();
    });
    addObject(object);

    device = alcOpenDevice((ByteBuffer) null);
    if (device == MemoryUtil.NULL) {
      throw new IllegalStateException("Failed to open OpenAL device");
    }

    context = alcCreateContext(device, (IntBuffer) null);
    alcMakeContextCurrent(context);
    AL.createCapabilities(ALC.createCapabilities(device));

    buffer = alGenBuffers();

    channels = MemoryUtil.memAllocInt(1);
    sampleRate = MemoryUtil.memAllocInt(1);

    pcm = STBVorbis.stb_vorbis_decode_filename(Files.internal("/audio/MyVoice.ogg").getAbsolutePath(),
        channels, sampleRate);

    if (pcm == null)
      try {
        throw new Exception("Not working");
      } catch (Exception e) {
        throw new RuntimeException(e);
      }

    source = alGenSources();

    int format;
    if (channels.get() == 1) {
      format = AL_FORMAT_MONO16;
    } else {
      format = AL_FORMAT_STEREO16;
    }

    alBufferData(buffer, format, pcm, sampleRate.get());

    alSourcei(source, AL_BUFFER, buffer);

    alSourcef(source, AL_PITCH, 1.25f);
    alSourcei(source, AL_LOOPING, AL_TRUE);
    alSource3f(source, AL_DIRECTION, 10, 1, 100);
    // alListenerfv(AL_);
    alSourcePlay(source);
  }

  @Override
  public void dispose() {
    super.dispose();

    alDeleteSources(source);
    alDeleteBuffers(buffer);

    alcMakeContextCurrent(MemoryUtil.NULL);
    alcDestroyContext(context);
    alcCloseDevice(device);
    MemoryUtil.memFree(channels);
    MemoryUtil.memFree(sampleRate);
  }

  public static String getErrorName(int errorCode) {
    return switch (errorCode) {
      case AL_INVALID_NAME -> "AL_INVALID_NAME: Invalid name parameter";
      case AL_INVALID_ENUM -> "AL_INVALID_ENUM: Invalid enum parameter";
      case AL_INVALID_VALUE -> "AL_INVALID_VALUE: Invalid value parameter";
      case AL_INVALID_OPERATION -> "AL_INVALID_OPERATION: Invalid operation";
      case AL_OUT_OF_MEMORY -> "AL_OUT_OF_MEMORY: Out of memory";
      case AL_NO_ERROR -> "AL_NO_ERROR: No error";
      default -> "Unknown error code: " + errorCode;
    };
  }

  @Override
  public void loopSetup() {
    super.loopSetup();

    object.initGui("#version 330", true);

    ImGuiIO io = object.getIo();

    font = io.getFonts().addFontFromFileTTF(
        Files.internal("/fonts/JetBrainsMono.ttf").getAbsolutePath(), 24);

    // 🔥 VERY IMPORTANT
    object.getImGuiGl3().updateFontsTexture();

    ImGui.styleColorsDark();
  }

  @Override
  public void draw() {
    object.newFrame();
    super.draw();
  }

  @Test
  void ImGuiTest() {
    new ImGUITest().initialStart();
  }

}
