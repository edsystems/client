package com.eds.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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

    private Stage stage;
	
	@Override
	public void create () {
        Gdx.gl.glClearColor(0.3f, 0.3f, 0.3f, 1);
		stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        VisUI.load();
        RowLayout rowLayout = new RowLayout(new Padding(0, 0, 0, 5));

        final VisCheckBox debugViewCheckBox = new VisCheckBox("hello world");
        debugViewCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {

            }
        });

        final Padding padding = new Padding(2, 3);
        GridTableBuilder builder = new GridTableBuilder(padding, 2);

        builder.append(debugViewCheckBox);
        builder.row();

        builder.append(new VisLabel("title label"));
        builder.row();

        builder.append(new VisLabel("path"));
        builder.append(rowLayout, CellWidget.builder().fillX(),
                CellWidget.of(new VisTextField()).expandX().fillX().wrap(),
                CellWidget.of(new VisTextButton("choose")).padding(new Padding(0, 0)).wrap());
        builder.row();

        builder.append(new VisLabel("name"));
        builder.append(CellWidget.of(new VisTextField()).expandX().fillX().wrap());
        builder.row();

        builder.append(new VisLabel("description"));
        builder.append(CellWidget.of(new VisTextField()).fillX().wrap());
        builder.row();

        Table table = builder.build();
        table.setFillParent(true);
        stage.addActor(table);

	}


    private class RowLayout implements ActorLayout {
        private Padding padding;

        public RowLayout (Padding padding) {
            this.padding = padding;
        }

        @Override
        public Actor convertToActor (Actor... widgets) {
            return convertToActor(CellWidget.wrap(widgets));
        }

        @Override
        public Actor convertToActor (CellWidget<?>... widgets) {
            OneRowTableBuilder builder = new OneRowTableBuilder(padding);

            for (CellWidget<?> widget : widgets)
                builder.append(widget);

            return builder.build();
        }
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
