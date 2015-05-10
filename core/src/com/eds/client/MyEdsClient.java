package com.eds.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.building.GridTableBuilder;
import com.kotcrab.vis.ui.building.OneRowTableBuilder;
import com.kotcrab.vis.ui.building.utilities.CellWidget;
import com.kotcrab.vis.ui.building.utilities.Padding;
import com.kotcrab.vis.ui.building.utilities.layouts.ActorLayout;
import com.kotcrab.vis.ui.widget.VisCheckBox;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisTextField;

public class MyEdsClient extends ApplicationAdapter {

    public static final String LOG_TAG = "EdsClient";

    private Platform platform;

    private Array<String> filters = new Array<String>(new String[] {"Filtro1", "Filtro2"});
    private Stage stage;

    public MyEdsClient(Platform platform) {
        this.platform = platform;
    }

	@Override
	public void create () {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		stage = new Stage(new ExtendViewport(480, 800));
        //stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        VisUI.load();
        Gdx.app.log(MyEdsClient.LOG_TAG, "Skin textures: ");
        for (Texture texture : VisUI.getSkin().getAtlas().getTextures()) {
            Gdx.app.log(MyEdsClient.LOG_TAG, "texture = " + texture);
            texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        }

        float defaultSpace = 40f;
        float halfSpace = defaultSpace * .5f;
        Table table = new Table();

        final Image image = new Image();
        image.setUserObject("");
        image.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                platform.showImage(image.getUserObject().toString());
            }
        });
        image.setScaling(Scaling.fit);
        table.add(image).pad(defaultSpace).maxHeight(Value.percentWidth(1, table));
        table.row();

        final Platform.FileListener listener = new Platform.FileListener() {
            @Override
            public void fileChosen(String path) {
                if(path != null && !path.trim().isEmpty()) {
                    FileHandle fileHandle = Gdx.files.absolute(path);
                    if(fileHandle.exists() && !fileHandle.isDirectory()) {
                        Drawable drawable = image.getDrawable();
                        if (drawable instanceof TextureRegionDrawable) {
                            TextureRegionDrawable textureDrawable = (TextureRegionDrawable) drawable;
                            textureDrawable.getRegion().getTexture().dispose();
                        }
                        Texture texture = new Texture(fileHandle);
                        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
                        TextureRegionDrawable imageDrawable = new TextureRegionDrawable(new TextureRegion(texture));
                        image.setDrawable(imageDrawable);
                        image.setUserObject(path);
                    }
                }
            }

        };

        TextButton choose = new TextButton("Choose an image", VisUI.getSkin());
        choose.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                platform.chooseImage(listener);
            }
        });
        table.add(choose).space(defaultSpace).pad(defaultSpace).expand().fill().minHeight(defaultSpace);
        table.row();

        ButtonGroup group = new ButtonGroup();
        for (String filter : filters) {
            VisCheckBox filterCheckBox = new VisCheckBox(filter);
            filterCheckBox.addListener(new ChangeListener() {
                @Override
                public void changed (ChangeEvent event, Actor actor) {

                }
            });
            group.add(filterCheckBox);
            table.add(filterCheckBox).space(halfSpace);
            table.row();
        }

        TextButton process = new TextButton("Process", VisUI.getSkin());
        table.add(process).space(defaultSpace).pad(defaultSpace).expand().fill().minHeight(defaultSpace);
        process.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // TODO send the processed file
            }
        });

        ScrollPane pane = new ScrollPane(table);
        pane.setFillParent(true);
        pane.setScrollingDisabled(true, false);
        stage.addActor(pane);
	}

	@Override
	public void render () {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act();
        stage.draw();

	}

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        VisUI.dispose();
    }
}
