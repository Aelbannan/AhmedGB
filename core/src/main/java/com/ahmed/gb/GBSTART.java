package com.ahmed.gb;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class GBSTART extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	GameBoy gameBoy = new GameBoy();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		Pixmap map = new Pixmap(160,144, Pixmap.Format.RGBA8888);
		gameBoy.begin();
	}

	@Override
	public void render () {
		gameBoy.update();
	}
}
